package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataSala(
    @SerialName("id_sala") val id_sala: Int = 0,
    @SerialName("id_tipo_sala") val id_tipo_sala: Int = 0,
    @SerialName("tipo") val tipo: String = "",
    @SerialName("nombre") val nombre: String = "",
    @SerialName("capacidad") val capacidad: Int = 0,
    @SerialName("precio_hora") val precio_hora: Double = 0.0,
    @SerialName("precio_dia") val precio_dia: Double = 0.0,
    @SerialName("imagen") val imagen: String = "",
    @SerialName("descuento") val descuento: Double = 0.0
)
