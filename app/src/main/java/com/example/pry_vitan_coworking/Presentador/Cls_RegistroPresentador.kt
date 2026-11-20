package com.example.pry_vitan_coworking.Presentador

import android.util.Patterns
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloRegistro
import com.example.pry_vitan_coworking.Vista.RegistroActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//A02 Crear cuenta: cliente o empresa; la empresa no lleva apellidos
class Cls_RegistroPresentador(private val vista: RegistroActivity) {
    private val modelo = Cls_ModeloRegistro()

    //Cuando el usuario cambia el tipo de cuenta
    fun cambiarTipo(esEmpresa: Boolean) {
        //con empresa se desactivan (y se limpian) los apellidos
        vista.activarApellidos(!esEmpresa)
    }

    fun crearCuenta(esEmpresa: Boolean, nombre: String, ap: String, am: String, correo: String,
                    telefono: String, contrasena: String, pregunta: String, respuesta: String) {
        val tipo = if (esEmpresa) "empresa" else "cliente"

        //1 Validaciones (la API vuelve a revisar lo mismo)
        if (nombre.isEmpty()) {
            vista.mostrarErrorCampo("nombre", "Escribe el nombre")
            return
        }
        if (!esEmpresa && ap.isEmpty()) {
            vista.mostrarErrorCampo("ap", "Escribe el apellido paterno")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            vista.mostrarErrorCampo("correo", "El correo no es valido")
            return
        }
        if (telefono.length != 10) {
            vista.mostrarErrorCampo("telefono", "El teléfono debe tener 10 dígitos")
            return
        }
        if (contrasena.length < 8) {
            vista.mostrarErrorCampo("contrasena", "Mínimo 8 caracteres")
            return
        }
        if (respuesta.isEmpty()) {
            vista.mostrarErrorCampo("respuesta", "Escribe la respuesta a tu pregunta")
            return
        }

        //2 Mandar a registro.php (empresa manda los apellidos vacios y la API los guarda en NULL)
        vista.mostrarCargando(true)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    modelo.crearCuenta(tipo, nombre, if (esEmpresa) "" else ap, if (esEmpresa) "" else am,
                        correo, telefono, contrasena, pregunta, respuesta)
                }
                vista.mostrarCargando(false)
                vista.mostrarMensaje(resultado.mensaje)
                if (resultado.exito) {
                    //regresa al login
                    vista.regresarAlLogin()
                }
            } catch (e: Exception) {
                vista.mostrarCargando(false)
                vista.mostrarMensaje("Revisa tu conexión a internet")
            }
        }
    }
}
