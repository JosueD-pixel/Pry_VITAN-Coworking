package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("exito") val exito: Boolean = false,
    @SerialName("mensaje") val mensaje: String = "",
    @SerialName("id_usuario") val id_usuario: Int = 0,
    @SerialName("id_rol") val id_rol: Int = 0,
    @SerialName("rol") val rol: String = "",
    @SerialName("nombre") val nombre: String = "",
    @SerialName("ap") val ap: String = "",
    @SerialName("correo") val correo: String = ""
)
