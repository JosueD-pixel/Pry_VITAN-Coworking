package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservarRequest(
    @SerialName("id_usuario") val idUsuario: Int,
    @SerialName("id_sala") val idSala: Int,
    @SerialName("fecha") val fecha: String,
    @SerialName("hora_inicio") val horaInicio: String,
    @SerialName("hora_fin") val horaFin: String,
    @SerialName("motivo") val motivo: String
)