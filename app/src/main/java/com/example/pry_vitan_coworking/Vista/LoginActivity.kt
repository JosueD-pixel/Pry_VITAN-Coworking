package com.example.pry_vitan_coworking.Vista

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pry_vitan_coworking.Presentador.Cls_LoginPresentador
import com.example.pry_vitan_coworking.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

//A01 Iniciar sesion (cliente, empresa y administrador)
class LoginActivity : AppCompatActivity() {
    private lateinit var presentador: Cls_LoginPresentador
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilContrasena: TextInputLayout
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var btnIniciarSesion: Button
    private lateinit var txtOlvidaste: TextView
    private lateinit var txtCrearCuenta: TextView
    private lateinit var pbCargando: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //enlazar componentes XML
        tilCorreo = findViewById(R.id.tilCorreo)
        tilContrasena = findViewById(R.id.tilContrasena)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        txtOlvidaste = findViewById(R.id.txtOlvidaste)
        txtCrearCuenta = findViewById(R.id.txtCrearCuenta)
        pbCargando = findViewById(R.id.pbCargando)

        //inicializar presentador
        presentador = Cls_LoginPresentador(this)

        btnIniciarSesion.setOnClickListener(this::ejecutarIniciarSesion)
        txtCrearCuenta.setOnClickListener { startActivity(Intent(this, RegistroActivity::class.java)) }
        //Recuperar cuenta se agrega despues; por ahora solo avisa
        txtOlvidaste.setOnClickListener { mostrarMensaje("Muy pronto podrás recuperar tu cuenta") }
    }

    fun ejecutarIniciarSesion(v: View) {
        tilCorreo.error = null
        tilContrasena.error = null
        presentador.iniciarSesion(etCorreo.text.toString().trim(), etContrasena.text.toString())
    }

    fun mostrarErrorCorreo(mensaje: String) {
        tilCorreo.error = mensaje
    }

    fun mostrarErrorContrasena(mensaje: String) {
        tilContrasena.error = mensaje
    }

    fun mostrarCargando(cargando: Boolean) {
        pbCargando.visibility = if (cargando) View.VISIBLE else View.GONE
        btnIniciarSesion.isEnabled = !cargando
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    //Guarda quien inicio sesion para que las otras pantallas lo usen (ej. reservar)
    fun guardarSesion(id_usuario: Int, id_rol: Int, nombre: String) {
        val preferencias = getSharedPreferences("sesion", MODE_PRIVATE)
        preferencias.edit()
            .putInt("id_usuario", id_usuario)
            .putInt("id_rol", id_rol)
            .putString("nombre", nombre)
            .apply()
    }

    //Cliente o empresa: C01 Salas (Nathan)
    fun abrirSalas() {
        startActivity(Intent(this, SalasActivity::class.java))
        finish()
    }

    //Administrador: cuando Christian suba D01 Reservas, cambiar CancelacionesActivity por ReservasActivity
    fun abrirAdministrador() {
        startActivity(Intent(this, CancelacionesActivity::class.java))
        finish()
    }
}
