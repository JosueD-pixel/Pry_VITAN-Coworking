package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservarResponse(
    @SerialName("ok") val ok: Boolean = false,
    @SerialName("mensaje") val mensaje: String = "",
    @SerialName("id_reserva") val idReserva: Int? = null,
    @SerialName("estado") val estado: String? = null,
    @SerialName("modalidad") val modalidad: String? = null,
    @SerialName("horas") val horas: Int? = null,
    @SerialName("subtotal") val subtotal: Double? = null,
    @SerialName("descuento_pct") val descuentoPct: Double? = null,
    @SerialName("descuento") val descuento: Double? = null,
    @SerialName("total") val total: Double? = null,
    @SerialName("anticipo") val anticipo: Double? = null
)