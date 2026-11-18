package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Christian · D02 Usuarios
// Un usuario de la lista que regresa listar_usuarios.php (nunca trae la contraseña).
// am, cp, calle, ciudad, colonia y telefono pueden llegar en null porque en la
// tabla usuarios son opcionales (el domicilio no se pide al crear la cuenta).
@Serializable
data class UsuarioResponse(
    @SerialName("id_usuario") val id_usuario: Int = 0,
    @SerialName("id_rol") val id_rol: Int = 0,             // 1 administrador, 2 cliente, 3 empresa
    @SerialName("rol") val rol: String = "",
    @SerialName("nombre") val nombre: String = "",
    @SerialName("ap") val ap: String = "",
    @SerialName("am") val am: String? = null,
    @SerialName("cp") val cp: String? = null,
    @SerialName("calle") val calle: String? = null,
    @SerialName("ciudad") val ciudad: String? = null,
    @SerialName("colonia") val colonia: String? = null,
    @SerialName("correo") val correo: String = "",
    @SerialName("telefono") val telefono: String? = null,
    @SerialName("favorito") val favorito: Boolean = false  // la estrella de la tarjeta
)
