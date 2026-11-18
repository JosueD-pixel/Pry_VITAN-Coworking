package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class dataCancelacion(
    @SerialName("id_cancelacion") val id_cancelacion: Int = 0,
    @SerialName("id_reserva") val id_reserva: Int = 0,
    @SerialName("motivo") val motivo: String = "",
    @SerialName("penalizacion") val penalizacion: Double = 0.0,
    @SerialName("sala") val sala: String = "",
    @SerialName("cliente") val cliente: String = "",
    @SerialName("fecha") val fecha: String = ""
)
