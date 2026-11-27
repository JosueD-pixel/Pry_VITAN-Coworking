package com.example.pry_vitan_coworking.Modelo

import java.io.Serializable

data class Cls_ModeloReservar(
    val idSala: Int,
    val nombreSala: String,
    val fecha: String,          // AAAA-MM-DD
    val horaInicio: Int,        // 10 -> 10:00
    val horaFin: Int,           // 13 -> 13:00
    val precioHora: Double,
    val precioDia: Double,
    val descuentoPct: Double,   // 0.0 si no hay promoción
    val horaApertura: Int = 9,
    val horaCierre: Int = 22
) : Serializable {

    val horas: Int get() = horaFin - horaInicio
    val esDiaCompleto: Boolean get() = horaInicio == horaApertura && horaFin == horaCierre
    val subtotal: Double get() = if (esDiaCompleto) precioDia else horas * precioHora
    val descuento: Double get() = subtotal * descuentoPct / 100.0
    val total: Double get() = subtotal - descuento
    val anticipo: Double get() = total * 0.5

    fun textoInicio() = String.format("%02d:00", horaInicio)
    fun textoFin() = String.format("%02d:00", horaFin)

    fun aRequest(idUsuario: Int, motivo: String) = ReservarRequest(
        idUsuario = idUsuario,
        idSala = idSala,
        fecha = fecha,
        horaInicio = textoInicio(),
        horaFin = textoFin(),
        motivo = motivo
    )
}