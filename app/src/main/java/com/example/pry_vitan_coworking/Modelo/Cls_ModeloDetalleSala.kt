package com.example.pry_vitan_coworking.Modelo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Cls_ModeloDetalleSala(
    @SerialName("id_sala") val idSala: Int = 0,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("tipo") val tipo: String = "",
    @SerialName("capacidad") val capacidad: Int = 0,
    @SerialName("descripcion") val descripcion: String? = null,
    @SerialName("precio_hora") val precioHora: Double = 0.0,
    @SerialName("precio_dia") val precioDia: Double = 0.0,
    @SerialName("imagenes") val imagenes: List<String> = emptyList(),
    @SerialName("mobiliario") val mobiliario: List<Cls_MobiliarioSala> = emptyList(),
    @SerialName("promocion") val promocion: Cls_PromocionSala? = null,
    @SerialName("video") val video: String? = null,
    @SerialName("hora_apertura") val horaApertura: Int = 9,
    @SerialName("hora_cierre") val horaCierre: Int = 22,
    @SerialName("horas_minimas") val horasMinimas: Int = 2
)

@Serializable
data class Cls_MobiliarioSala(
    @SerialName("id_mobiliario") val idMobiliario: Int = 0,
    @SerialName("marca") val marca: String? = null,
    @SerialName("nombre") val nombre: String = "",
    @SerialName("descripcion") val descripcion: String? = null
)

@Serializable
data class Cls_PromocionSala(
    @SerialName("id_promocion") val idPromocion: Int = 0,
    @SerialName("descuento") val descuento: Double = 0.0,
    @SerialName("fecha_inicio") val fechaInicio: String = "",
    @SerialName("fecha_fin") val fechaFin: String = ""
)