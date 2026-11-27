package com.example.pry_vitan_coworking.Vista

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloReservar
import com.example.pry_vitan_coworking.Modelo.ReservarResponse
import com.example.pry_vitan_coworking.Presentador.Cls_ReservarPresentador
import com.example.pry_vitan_coworking.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Locale

// Confirmar reserva: resumen (sala, día, horas, precio, promoción, total y
// anticipo del 50 %), el motivo y el botón Confirmar.
class ConfirmarReservaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESERVA = "reserva"
    }

    private lateinit var presentador: Cls_ReservarPresentador
    private lateinit var progreso: CircularProgressIndicator
    private lateinit var txtMensaje: TextView
    private lateinit var txtMotivo: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var reserva: Cls_ModeloReservar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_confirmar_reserva)

        // La barra azul llega hasta arriba; abajo deja espacio para botones y teclado
        val encabezado: View = findViewById(R.id.encabezado)
        val paddingSuperior = encabezado.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(systemBars.left, 0, systemBars.right, maxOf(systemBars.bottom, teclado.bottom))
            encabezado.updatePadding(top = paddingSuperior + systemBars.top)
            insets
        }
        // Hora y batería en blanco para que se vean sobre el azul
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        progreso = findViewById(R.id.progreso)
        txtMensaje = findViewById(R.id.txtMensaje)
        txtMotivo = findViewById(R.id.txtMotivo)
        btnConfirmar = findViewById(R.id.btnConfirmar)
        findViewById<ImageButton>(R.id.btnRegresar).setOnClickListener { finish() }

        presentador = Cls_ReservarPresentador(this)

        val recibida = leerReserva()
        if (recibida == null) {
            mostrarError("No se recibieron los datos de la reserva")
            btnConfirmar.isEnabled = false
            return
        }
        reserva = recibida
        mostrarResumen()

        btnConfirmar.setOnClickListener {
            presentador.confirmar(reserva, idUsuarioSesion(), txtMotivo.text.toString())
        }
    }

    fun mostrarCargando(cargando: Boolean) {
        progreso.visibility = if (cargando) View.VISIBLE else View.GONE
        btnConfirmar.isEnabled = !cargando
        if (cargando) txtMensaje.visibility = View.GONE
    }

    fun reservaExitosa(respuesta: ReservarResponse) {
        txtMensaje.visibility = View.GONE
        AlertDialog.Builder(this)
            .setTitle("¡Reserva registrada!")
            .setMessage(
                "Folio: ${respuesta.idReserva}\n" +
                        "Total: ${dinero(respuesta.total ?: reserva.total)}\n" +
                        "Anticipo (50 %): ${dinero(respuesta.anticipo ?: reserva.anticipo)}\n\n" +
                        respuesta.mensaje
            )
            .setCancelable(false)
            .setPositiveButton("Aceptar") { _, _ ->
                setResult(Activity.RESULT_OK)
                finish()
            }
            .show()
    }

    fun mostrarError(mensaje: String) {
        txtMensaje.text = mensaje
        txtMensaje.visibility = View.VISIBLE
    }


    // Lee la reserva que mandó DetalleSalaActivity
    private fun leerReserva(): Cls_ModeloReservar? =
        if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(EXTRA_RESERVA, Cls_ModeloReservar::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_RESERVA) as? Cls_ModeloReservar
        }


    private fun idUsuarioSesion(): Int =
        getSharedPreferences("sesion", MODE_PRIVATE).getInt("id_usuario", 0)

    private fun dinero(valor: Double) = String.format(Locale.US, "$%,.2f", valor)

    private fun mostrarResumen() {
        val r = reserva
        findViewById<TextView>(R.id.txtSala).text = r.nombreSala
        findViewById<TextView>(R.id.txtFecha).text = r.fecha.split("-").reversed().joinToString("/")
        findViewById<TextView>(R.id.txtHorario).text = "${r.textoInicio()} a ${r.textoFin()} (${r.horas} h)"
        findViewById<TextView>(R.id.txtTarifa).text =
            if (r.esDiaCompleto) "Día completo: ${dinero(r.precioDia)}"
            else "${r.horas} h × ${dinero(r.precioHora)}"
        findViewById<TextView>(R.id.txtSubtotal).text = dinero(r.subtotal)

        val filaDescuento: View = findViewById(R.id.filaDescuento)
        if (r.descuentoPct > 0) {
            filaDescuento.visibility = View.VISIBLE
            findViewById<TextView>(R.id.lblDescuento).text =
                String.format(Locale.US, "Promoción (%.0f%%)", r.descuentoPct)
            findViewById<TextView>(R.id.txtDescuento).text = "- ${dinero(r.descuento)}"
        } else {
            filaDescuento.visibility = View.GONE
        }
        findViewById<TextView>(R.id.txtTotal).text = dinero(r.total)
        findViewById<TextView>(R.id.txtAnticipo).text = dinero(r.anticipo)
    }

    override fun onDestroy() {
        if (::presentador.isInitialized) presentador.destruir()
        super.onDestroy()
    }
}