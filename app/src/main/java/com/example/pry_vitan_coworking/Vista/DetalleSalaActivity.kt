package com.example.pry_vitan_coworking.Vista

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Adaptador.Cls_GaleriaAdaptador
import com.example.pry_vitan_coworking.Adaptador.Cls_HorasAdaptador
import com.example.pry_vitan_coworking.Modelo.Cls_HoraDisponible
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloDetalleSala
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloReservar
import com.example.pry_vitan_coworking.Presentador.Cls_DetalleSalaPresentador
import com.example.pry_vitan_coworking.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Calendar
import java.util.Locale

// Detalle de la sala: fotos, mobiliario, video, día y horas libres, botón Reservar.
// Solo muestra lo que le entrega Cls_DetalleSalaPresentador.
// Se abre con: Intent(this, DetalleSalaActivity::class.java).putExtra("id_sala", idSala)
class DetalleSalaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID_SALA = "id_sala"
    }

    private lateinit var presentador: Cls_DetalleSalaPresentador
    private lateinit var adaptadorGaleria: Cls_GaleriaAdaptador
    private lateinit var adaptadorHoras: Cls_HorasAdaptador
    private lateinit var progreso: CircularProgressIndicator
    private lateinit var txtMensaje: TextView
    private lateinit var txtTitulo: TextView
    private lateinit var txtTipo: TextView
    private lateinit var txtPromocion: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtPrecios: TextView
    private lateinit var txtMobiliario: TextView
    private lateinit var lblVideo: TextView
    private lateinit var videoSala: VideoView
    private lateinit var btnFecha: Button
    private lateinit var txtSeleccion: TextView
    private lateinit var btnReservar: Button

    // Al confirmar la reserva regresa con RESULT_OK y se cierra este detalle
    private val confirmarLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
            if (r.resultCode == Activity.RESULT_OK) finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_sala)

        // La barra azul llega hasta arriba (detrás de la hora y la batería);
        // la barra inferior deja solo su espacio para los botones del teléfono.
        val encabezado: View = findViewById(R.id.encabezado)
        val paddingSuperior = encabezado.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            encabezado.updatePadding(top = paddingSuperior + systemBars.top)
            insets
        }
        // Hora y batería en blanco para que se vean sobre el azul
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        progreso = findViewById(R.id.progreso)
        txtMensaje = findViewById(R.id.txtMensaje)
        txtTitulo = findViewById(R.id.txtTitulo)
        txtTipo = findViewById(R.id.txtTipo)
        txtPromocion = findViewById(R.id.txtPromocion)
        txtDescripcion = findViewById(R.id.txtDescripcion)
        txtPrecios = findViewById(R.id.txtPrecios)
        txtMobiliario = findViewById(R.id.txtMobiliario)
        lblVideo = findViewById(R.id.lblVideo)
        videoSala = findViewById(R.id.videoSala)
        btnFecha = findViewById(R.id.btnFecha)
        txtSeleccion = findViewById(R.id.txtSeleccion)
        btnReservar = findViewById(R.id.btnReservar)
        findViewById<ImageButton>(R.id.btnRegresar).setOnClickListener { finish() }

        presentador = Cls_DetalleSalaPresentador(this)

        // Galería horizontal, una foto a la vez
        adaptadorGaleria = Cls_GaleriaAdaptador()
        val rvGaleria: RecyclerView = findViewById(R.id.rvGaleria)
        rvGaleria.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvGaleria.adapter = adaptadorGaleria
        PagerSnapHelper().attachToRecyclerView(rvGaleria)

        // Horas en cuadrícula de 4 columnas
        adaptadorHoras = Cls_HorasAdaptador { hora -> presentador.seleccionarHora(hora) }
        val rvHoras: RecyclerView = findViewById(R.id.rvHoras)
        rvHoras.layoutManager = GridLayoutManager(this, 4)
        rvHoras.isNestedScrollingEnabled = false
        rvHoras.adapter = adaptadorHoras

        btnFecha.setOnClickListener { elegirFecha() }
        btnReservar.setOnClickListener { presentador.reservar() }

        // Al abrir la pantalla se carga la sala que mandaron
        val idSala = intent.getIntExtra(EXTRA_ID_SALA, 0)
        if (idSala <= 0) {
            mostrarError("No se recibió la sala")
            return
        }
        presentador.cargarSala(idSala)
    }



    fun mostrarCargando(cargando: Boolean) {
        progreso.visibility = if (cargando) View.VISIBLE else View.GONE
    }

    fun mostrarSala(sala: Cls_ModeloDetalleSala) {
        txtMensaje.visibility = View.GONE
        txtTitulo.text = sala.nombre
        txtTipo.text = "${sala.tipo} · Capacidad: ${sala.capacidad} personas"
        txtDescripcion.text = sala.descripcion ?: ""
        txtPrecios.text = String.format(
            Locale.US, "$%,.2f por hora  ·  $%,.2f por día (9:00 a 22:00)",
            sala.precioHora, sala.precioDia
        )

        val promo = sala.promocion
        if (promo != null) {
            txtPromocion.visibility = View.VISIBLE
            txtPromocion.text = String.format(
                Locale.US, "Promoción: %.0f%% de descuento (hasta el %s)", promo.descuento, promo.fechaFin
            )
        } else {
            txtPromocion.visibility = View.GONE
        }

        adaptadorGaleria.actualizar(sala.imagenes)

        txtMobiliario.text = if (sala.mobiliario.isEmpty()) "Sin mobiliario registrado"
        else sala.mobiliario.joinToString("\n") { m ->
            "• ${m.nombre}" + (m.marca?.let { " ($it)" } ?: "")
        }

        val video = sala.video
        if (video.isNullOrEmpty()) {
            lblVideo.visibility = View.GONE
            videoSala.visibility = View.GONE
        } else {
            lblVideo.visibility = View.VISIBLE
            videoSala.visibility = View.VISIBLE
            val controles = MediaController(this)
            controles.setAnchorView(videoSala)
            videoSala.setMediaController(controles)
            videoSala.setVideoURI(Uri.parse(video))
            videoSala.setOnPreparedListener { it.isLooping = false; videoSala.seekTo(1) }
        }
        txtSeleccion.text = "Elige el día para ver las horas libres"
    }

    fun mostrarHoras(horas: List<Cls_HoraDisponible>, mensaje: String) {
        adaptadorHoras.actualizar(horas)
        if (horas.none { it.libre } && horas.isNotEmpty()) txtSeleccion.text = mensaje
    }

    fun mostrarSeleccion(inicio: Int?, fin: Int?, texto: String) {
        adaptadorHoras.marcarSeleccion(inicio, fin)
        txtSeleccion.text = texto
    }

    fun mostrarError(mensaje: String) {
        txtMensaje.text = mensaje
        txtMensaje.visibility = View.VISIBLE
    }

    fun irAConfirmar(reserva: Cls_ModeloReservar) {
        txtMensaje.visibility = View.GONE
        val intent = Intent(this, ConfirmarReservaActivity::class.java)
        intent.putExtra(ConfirmarReservaActivity.EXTRA_RESERVA, reserva)
        confirmarLauncher.launch(intent)
    }



    // Calendario: de hoy a 3 meses
    private fun elegirFecha() {
        val hoy = Calendar.getInstance()
        val dialogo = DatePickerDialog(this, { _, anio, mes, dia ->
            val fecha = String.format(Locale.US, "%04d-%02d-%02d", anio, mes + 1, dia)
            btnFecha.text = String.format(Locale.US, "%02d/%02d/%04d", dia, mes + 1, anio)
            presentador.cambiarFecha(fecha)
        }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH))
        dialogo.datePicker.minDate = hoy.timeInMillis
        dialogo.datePicker.maxDate = hoy.timeInMillis + 90L * 24 * 60 * 60 * 1000
        dialogo.show()
    }

    override fun onPause() {
        super.onPause()
        if (videoSala.isPlaying) videoSala.pause()
    }

    override fun onDestroy() {
        if (::presentador.isInitialized) presentador.destruir()
        super.onDestroy()
    }
}