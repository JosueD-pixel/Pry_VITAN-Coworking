package com.example.pry_vitan_coworking.Presentador

import android.util.Patterns
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloLogin
import com.example.pry_vitan_coworking.Vista.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//A01 Iniciar sesion: valida los campos, llama a login.php y manda a cada quien a su parte
class Cls_LoginPresentador(private val vista: LoginActivity) {
    private val modelo = Cls_ModeloLogin()

    fun iniciarSesion(correo: String, contrasena: String) {
        //1 Validar antes de llamar a la API
        if (correo.isEmpty()) {
            vista.mostrarErrorCorreo("Escribe tu correo")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            vista.mostrarErrorCorreo("El correo no es valido")
            return
        }
        if (contrasena.isEmpty()) {
            vista.mostrarErrorContrasena("Escribe tu contraseña")
            return
        }

        //2 Llamar a la API sin bloquear la pantalla
        vista.mostrarCargando(true)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    modelo.iniciarSesion(correo, contrasena)
                }
                vista.mostrarCargando(false)
                if (respuesta.exito) {
                    //3 Guardar quien entro y abrir su pantalla segun el rol
                    vista.guardarSesion(respuesta.id_usuario, respuesta.id_rol, respuesta.nombre)
                    vista.mostrarMensaje(respuesta.mensaje)
                    if (respuesta.id_rol == 1) {
                        vista.abrirAdministrador()
                    } else {
                        vista.abrirSalas()
                    }
                } else {
                    //"Correo o contraseña incorrectos"
                    vista.mostrarMensaje(respuesta.mensaje)
                }
            } catch (e: Exception) {
                vista.mostrarCargando(false)
                vista.mostrarMensaje("Revisa tu conexión a internet")
            }
        }
    }
}
