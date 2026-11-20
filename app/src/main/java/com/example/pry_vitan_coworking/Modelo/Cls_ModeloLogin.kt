package com.example.pry_vitan_coworking.Modelo

class Cls_ModeloLogin {
    suspend fun iniciarSesion(correo: String, contrasena: String): LoginResponse {
        val request = LoginRequest(correo = correo, contrasena = contrasena)
        return RetrofitCliente.apiService.iniciarSesion(request)
    }
}
