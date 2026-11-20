package com.example.pry_vitan_coworking.Vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Adaptador.Cls_AdaptadorAdminReservas
import com.example.pry_vitan_coworking.Modelo.Cls_Reserva
import com.example.pry_vitan_coworking.Presentador.Cls_AdminReservasPresentador
import com.example.pry_vitan_coworking.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator

class AdminReservasActivity : AppCompatActivity() {

    private lateinit var presentador: Cls_AdminReservasPresentador
    private lateinit var adaptador: Cls_AdaptadorAdminReservas
    private lateinit var progreso: CircularProgressIndicator
    private lateinit var txtMensaje: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_reservas)

        // La barra azul llega hasta arriba (detrás de la hora y la batería);
        // la barra inferior deja sola su espacio para los botones del teléfono.
        val encabezado: View = findViewById(R.id.encabezado)
        val paddingSuperior = encabezado.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            encabezado.updatePadding(top = paddingSuperior + systemBars.top)
            insets
        }
        // Hora y batería en blanco para que se vean sobre el azul
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        progreso = findViewById(R.id.progreso)
        txtMensaje = findViewById(R.id.txtMensaje)

        // Lista de reservas
        val rvReservas: RecyclerView = findViewById(R.id.rvReservas)
        rvReservas.layoutManager = LinearLayoutManager(this)
        adaptador = Cls_AdaptadorAdminReservas(emptyList())
        rvReservas.adapter = adaptador

        presentador = Cls_AdminReservasPresentador(this)

        // Chips → filtro por estado (lo hace la API)
        findViewById<ChipGroup>(R.id.grupoEstados).setOnCheckedStateChangeListener { _, idsElegidos ->
            presentador.cargarReservas(estadoDelChip(idsElegidos.firstOrNull()))
        }

        // Buscador → búsqueda por cliente o sala (la hace el presentador sobre la lista)
        findViewById<EditText>(R.id.txtBuscar).doAfterTextChanged { texto ->
            presentador.buscar(texto?.toString() ?: "")
        }

        findViewById<ImageButton>(R.id.btnCerrarSesion).setOnClickListener { cerrarSesion() }
        configurarBarraInferior()

        // Al abrir la pantalla se muestran todas las reservas
        presentador.cargarReservas("")
    }

    // ---------- Funciones que llama el presentador ----------

    fun mostrarCargando(cargando: Boolean) {
        progreso.visibility = if (cargando) View.VISIBLE else View.GONE
        if (cargando) txtMensaje.visibility = View.GONE
    }

    fun mostrarReservas(reservas: List<Cls_Reserva>) {
        txtMensaje.visibility = View.GONE
        adaptador.actualizarLista(reservas)
    }

    fun mostrarSinReservas() {
        adaptador.actualizarLista(emptyList())
        txtMensaje.text = "No hay reservas"
        txtMensaje.visibility = View.VISIBLE
    }

    fun mostrarError(mensaje: String) {
        adaptador.actualizarLista(emptyList())
        txtMensaje.text = mensaje
        txtMensaje.visibility = View.VISIBLE
    }

    // ---------- Ayudantes de la pantalla ----------

    // Chip elegido → valor que espera consultar_reservas.php
    private fun estadoDelChip(idChip: Int?): String = when (idChip) {
        R.id.chipPendientes -> "pendiente"
        R.id.chipConfirmadas -> "confirmada"
        R.id.chipFinalizadas -> "finalizada"
        R.id.chipCanceladas -> "cancelada"
        else -> ""   // Todas
    }

    private fun configurarBarraInferior() {
        val barra: BottomNavigationView = findViewById(R.id.barraInferior)
        barra.selectedItemId = R.id.nav_reservas
        barra.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_reservas -> true   // ya estamos en Reservas
                R.id.nav_usuarios -> {
                    abrirPestana(AdminUsuariosActivity::class.java)
                    true
                }
                R.id.nav_cancelaciones -> {
                    abrirPestana(CancelacionesActivity::class.java)
                    true
                }
                else -> false
            }
        }
    }

    // Cambia de pestaña sin animación y cierra esta pantalla,
    // para que no se acumulen pantallas al ir y venir.
    private fun abrirPestana(pantalla: Class<*>) {
        val intent = Intent(this, pantalla)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    // Regresa al inicio de sesión y borra el historial de pantallas, para que con
    // "Atrás" no se pueda volver a la consulta. Si el login guarda la sesión
    // (por ejemplo en SharedPreferences), aquí también hay que borrarla.
    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
