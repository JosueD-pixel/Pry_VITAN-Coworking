package com.example.pry_vitan_coworking.Vista

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
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
        bnvAdmin.selectedItemId = R.id.menuCancelaciones
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

    //pendiente abrir Reservas y Usuarios cuando existan
    fun ejecutarCambiarPestana(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuReservas -> {
                Toast.makeText(this, "Reservas (pendiente)", Toast.LENGTH_SHORT).show()
                return false
            }
            R.id.menuUsuarios -> {
                Toast.makeText(this, "Usuarios (pendiente)", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    //pendiente borrar la sesion guardada y regresar al login
    fun ejecutarCerrarSesion(v: View) {
        finish()
    }
}
