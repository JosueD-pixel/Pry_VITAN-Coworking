package com.example.pry_vitan_coworking.InterfaceApi

import com.example.pry_vitan_coworking.Modelo.ReservaResponse
import com.example.pry_vitan_coworking.Modelo.UsuarioResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // ---------- Christian · Administrador ----------

    // D01 Reservas: estado = pendiente, confirmada, finalizada o cancelada ("" trae todas)
    @GET("consultar_reservas.php")
    suspend fun consultarReservas(@Query("estado") estado: String): List<ReservaResponse>

    // D02 Usuarios: id_rol = 1 administrador, 2 cliente o 3 empresa (0 trae todos)
    @GET("listar_usuarios.php")
    suspend fun listarUsuarios(@Query("id_rol") id_rol: Int): List<UsuarioResponse>
}
