package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class dataTipoSala(
    @SerialName("id_tipo_sala") val id_tipo_sala: Int = 0,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("descripcion") val descripcion: String = ""
)
{
    override fun toString(): String {
        return nombre
    }
}
