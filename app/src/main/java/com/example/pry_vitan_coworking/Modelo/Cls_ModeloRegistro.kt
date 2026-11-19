package com.example.pry_vitan_coworking.Modelo

class Cls_ModeloRegistro {
    suspend fun crearCuenta(tipo: String, nombre: String, ap: String, am: String, correo: String, telefono: String, contrasena: String): RegistroResponse {
        val request = RegistroRequest(tipo = tipo, nombre = nombre, ap = ap, am = am, correo = correo, telefono = telefono, contrasena = contrasena)
        return RetrofitCliente.apiService.crearCuenta(request)
    }
}
