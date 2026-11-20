package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Una reserva de la lista que regresa consultar_reservas.php.
// fecha_uso, hora_inicio y hora_fin pueden llegar en null si la reserva
// no tiene horario (la API usa LEFT JOIN con horarios).
@Serializable
data class Cls_Reserva(
    @SerialName("id_reserva") val id_reserva: Int = 0,
    @SerialName("fecha") val fecha: String = "",           // día en que se hizo la reserva
    @SerialName("estado") val estado: String = "",         // pendiente, confirmada, finalizada o cancelada
    @SerialName("modalidad") val modalidad: String = "",   // hora o dia
    @SerialName("motivo") val motivo: String = "",
    @SerialName("sala") val sala: String = "",
    @SerialName("cliente") val cliente: String = "",
    @SerialName("fecha_uso") val fecha_uso: String? = null,     // día en que se usa la sala (AAAA-MM-DD)
    @SerialName("hora_inicio") val hora_inicio: String? = null, // HH:MM:SS
    @SerialName("hora_fin") val hora_fin: String? = null        // HH:MM:SS
)
