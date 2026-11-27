package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HorariosResponse(
    @SerialName("ok") val ok: Boolean = false,
    @SerialName("mensaje") val mensaje: String = "",
    @SerialName("fecha") val fecha: String? = null,
    @SerialName("horas_libres") val horasLibres: Int = 0,
    @SerialName("horas") val horas: List<Cls_HoraDisponible>? = null
)

@Serializable
data class Cls_HoraDisponible(
    @SerialName("hora") val hora: Int = 0,
    @SerialName("inicio") val inicio: String = "",
    @SerialName("fin") val fin: String = "",
    @SerialName("libre") val libre: Boolean = false
)