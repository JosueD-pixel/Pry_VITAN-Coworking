package com.example.pry_vitan_coworking.Vista

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Adaptador.clsCancelacionesAdaptador
import com.example.pry_vitan_coworking.Modelo.dataCancelacion
import com.example.pry_vitan_coworking.Presentador.clsCancelacionesPresentador
import com.example.pry_vitan_coworking.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class CancelacionesActivity : AppCompatActivity() {
    private lateinit var presentador: clsCancelacionesPresentador
    private lateinit var rvCancelaciones: RecyclerView
    private lateinit var txtSinCancelaciones: TextView
    private lateinit var btnCerrarSesion: ImageButton
    private lateinit var bnvAdmin: BottomNavigationView
    private lateinit var adaptador: clsCancelacionesAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cancelaciones)
        //Encabezado azul hasta arriba (detras de la hora y bateria), igual que Christian
        val tbBarra: View = findViewById(R.id.tbBarra)
        val paddingSuperior = tbBarra.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            tbBarra.updatePadding(top = paddingSuperior + systemBars.top)
            insets
        }
        //Hora y bateria en blanco para que se vean sobre el azul
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        //enlazar componentes XML
        rvCancelaciones = findViewById(R.id.rvCancelaciones)
        txtSinCancelaciones = findViewById(R.id.txtSinCancelaciones)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)
        bnvAdmin = findViewById(R.id.bnvAdmin)

        adaptador = clsCancelacionesAdaptador(emptyList())
        rvCancelaciones.layoutManager = LinearLayoutManager(this)
        rvCancelaciones.adapter = adaptador

        btnCerrarSesion.setOnClickListener(this::ejecutarCerrarSesion)

        //barra inferior del admin
        bnvAdmin.selectedItemId = R.id.nav_cancelaciones
        bnvAdmin.setOnItemSelectedListener(this::ejecutarCambiarPestana)

        //inicializar presentador
        presentador = clsCancelacionesPresentador(this)
        presentador.cargarCancelaciones()
    }

    fun mostrarCancelaciones(cancelaciones: List<dataCancelacion>) {
        txtSinCancelaciones.visibility = View.GONE
        rvCancelaciones.visibility = View.VISIBLE
        adaptador.actualizarLista(cancelaciones)
    }

    fun mostrarSinCancelaciones() {
        adaptador.actualizarLista(emptyList())
        rvCancelaciones.visibility = View.GONE
        txtSinCancelaciones.visibility = View.VISIBLE
    }

    fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    //Barra inferior: abre Reservas (D01) o Usuarios (D02) de Christian; Cancelaciones ya es esta pantalla
    fun ejecutarCambiarPestana(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_reservas -> abrirPestana(AdminReservasActivity::class.java)
            R.id.nav_usuarios -> abrirPestana(AdminUsuariosActivity::class.java)
        }
        return true
    }

    //Cambia de pestana sin animacion y cierra esta pantalla para que no se acumulen (igual que Christian)
    private fun abrirPestana(pantalla: Class<*>) {
        val intent = Intent(this, pantalla)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    //pendiente borrar la sesion guardada y regresar al login
    fun ejecutarCerrarSesion(v: View) {
        finish()
    }
}
