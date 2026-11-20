package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataPromocion(
    @SerialName("id_promocion") val id_promocion: Int = 0,
    @SerialName("descuento") val descuento: Double = 0.0,
    @SerialName("estado") val estado: String = "",
    @SerialName("fecha_inicio") val fecha_inicio: String = "",
    @SerialName("fecha_fin") val fecha_fin: String = ""
)
