package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistroRequest(
    @SerialName("tipo") val tipo: String = "",          // "cliente" o "empresa"
    @SerialName("nombre") val nombre: String = "",
    @SerialName("ap") val ap: String = "",
    @SerialName("am") val am: String = "",
    @SerialName("correo") val correo: String = "",
    @SerialName("telefono") val telefono: String = "",
    @SerialName("contrasena") val contrasena: String = ""
)
