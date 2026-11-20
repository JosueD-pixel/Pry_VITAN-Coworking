package com.example.pry_vitan_coworking.Modelo

class Cls_ModeloRegistro {
    suspend fun crearCuenta(tipo: String, nombre: String, ap: String, am: String, correo: String, telefono: String, contrasena: String, pregunta: String, respuesta: String): RegistroResponse {
        val request = RegistroRequest(tipo = tipo, nombre = nombre, ap = ap, am = am, correo = correo, telefono = telefono, contrasena = contrasena, pregunta_seguridad = pregunta, respuesta_seguridad = respuesta)
        return RetrofitCliente.apiService.crearCuenta(request)
    }
}
