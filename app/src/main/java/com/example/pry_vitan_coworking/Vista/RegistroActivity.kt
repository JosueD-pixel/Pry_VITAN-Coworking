package com.example.pry_vitan_coworking.Vista

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pry_vitan_coworking.Presentador.Cls_RegistroPresentador
import com.example.pry_vitan_coworking.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

//A02 Crear cuenta (cliente o empresa)
class RegistroActivity : AppCompatActivity() {
    private lateinit var presentador: Cls_RegistroPresentador
    private lateinit var rgTipo: RadioGroup
    private lateinit var rbEmpresa: RadioButton
    private lateinit var lblNombre: TextView
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilAp: TextInputLayout
    private lateinit var tilAm: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilTelefono: TextInputLayout
    private lateinit var tilContrasena: TextInputLayout
    private lateinit var tilRespuesta: TextInputLayout
    private lateinit var etNombre: TextInputEditText
    private lateinit var etAp: TextInputEditText
    private lateinit var etAm: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etRespuesta: TextInputEditText
    private lateinit var spnPregunta: Spinner
    private lateinit var btnCrearCuenta: Button
    private lateinit var btnRegresar: ImageButton
    private lateinit var pbCargando: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //enlazar componentes XML
        rgTipo = findViewById(R.id.rgTipo)
        rbEmpresa = findViewById(R.id.rbEmpresa)
        lblNombre = findViewById(R.id.lblNombre)
        tilNombre = findViewById(R.id.tilNombre)
        tilAp = findViewById(R.id.tilAp)
        tilAm = findViewById(R.id.tilAm)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilTelefono = findViewById(R.id.tilTelefono)
        tilContrasena = findViewById(R.id.tilContrasena)
        tilRespuesta = findViewById(R.id.tilRespuesta)
        etNombre = findViewById(R.id.etNombre)
        etAp = findViewById(R.id.etAp)
        etAm = findViewById(R.id.etAm)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        etContrasena = findViewById(R.id.etContrasena)
        etRespuesta = findViewById(R.id.etRespuesta)
        spnPregunta = findViewById(R.id.spnPregunta)
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        btnRegresar = findViewById(R.id.btnRegresar)
        pbCargando = findViewById(R.id.pbCargando)

        //inicializar presentador
        presentador = Cls_RegistroPresentador(this)

        //al cambiar Cliente/Empresa el presentador decide si se activan los apellidos
        rgTipo.setOnCheckedChangeListener { _, idMarcado ->
            presentador.cambiarTipo(idMarcado == R.id.rbEmpresa)
        }
        btnCrearCuenta.setOnClickListener(this::ejecutarCrearCuenta)
        btnRegresar.setOnClickListener { finish() }
    }

    fun ejecutarCrearCuenta(v: View) {
        limpiarErrores()
        presentador.crearCuenta(
            rbEmpresa.isChecked,
            etNombre.text.toString().trim(),
            etAp.text.toString().trim(),
            etAm.text.toString().trim(),
            etCorreo.text.toString().trim(),
            etTelefono.text.toString().trim(),
            etContrasena.text.toString(),
            spnPregunta.selectedItem.toString(),
            etRespuesta.text.toString().trim()
        )
    }

    fun activarApellidos(activar: Boolean) {
        tilAp.isEnabled = activar
        tilAm.isEnabled = activar
        if (!activar) {
            etAp.setText("")
            etAm.setText("")
            tilAp.error = null
        }
        //la etiqueta y el texto gris cambian segun Cliente o Empresa
        if (activar) {
            lblNombre.text = getString(R.string.lblNombre)
            etAp.hint = getString(R.string.phAp)
            etAm.hint = getString(R.string.phAm)
        } else {
            lblNombre.text = getString(R.string.lblNombreEmpresa)
            etAp.hint = getString(R.string.phNoAplica)
            etAm.hint = getString(R.string.phNoAplica)
        }
    }

    //marca en rojo el campo que no cumple
    fun mostrarErrorCampo(campo: String, mensaje: String) {
        when (campo) {
            "nombre" -> tilNombre.error = mensaje
            "ap" -> tilAp.error = mensaje
            "correo" -> tilCorreo.error = mensaje
            "telefono" -> tilTelefono.error = mensaje
            "contrasena" -> tilContrasena.error = mensaje
            "respuesta" -> tilRespuesta.error = mensaje
        }
    }

    private fun limpiarErrores() {
        tilNombre.error = null
        tilAp.error = null
        tilCorreo.error = null
        tilTelefono.error = null
        tilContrasena.error = null
        tilRespuesta.error = null
    }

    fun mostrarCargando(cargando: Boolean) {
        pbCargando.visibility = if (cargando) View.VISIBLE else View.GONE
        btnCrearCuenta.isEnabled = !cargando
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    //la cuenta se creo: regresa a Iniciar sesion
    fun regresarAlLogin() {
        finish()
    }
}
