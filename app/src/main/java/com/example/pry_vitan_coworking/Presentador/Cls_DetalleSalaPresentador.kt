package com.example.pry_vitan_coworking.Presentador

import com.example.pry_vitan_coworking.Modelo.Cls_HoraDisponible
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloDetalleSala
import com.example.pry_vitan_coworking.Modelo.Cls_ModeloReservar
import com.example.pry_vitan_coworking.Modelo.DetalleSalaResponse
import com.example.pry_vitan_coworking.Modelo.HorariosResponse
import com.example.pry_vitan_coworking.Modelo.RetrofitCliente
import com.example.pry_vitan_coworking.Vista.DetalleSalaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException

// Detalle de sala: pide la sala y sus horarios, lleva las horas elegidas
// y valida antes de ir a confirmar.
class Cls_DetalleSalaPresentador(private val vista: DetalleSalaActivity) {

    private val api = RetrofitCliente.apiService
    private val scope = CoroutineScope(Dispatchers.Main)

    private var sala: Cls_ModeloDetalleSala? = null
    private var horas: List<Cls_HoraDisponible> = emptyList()
    private var fecha: String? = null
    // fin es inclusivo: inicio=10, fin=12 -> de 10:00 a 13:00
    private var inicio: Int? = null
    private var fin: Int? = null

    fun cargarSala(idSala: Int) {
        // 1 Llamar a la API sin bloquear la pantalla
        vista.mostrarCargando(true)
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { api.detalleSala(idSala) }
                val s = r.sala
                if (r.ok && s != null) {
                    sala = s
                    vista.mostrarSala(s)
                } else {
                    vista.mostrarError(r.mensaje.ifEmpty { "No se pudo cargar la sala" })
                }
            } catch (e: HttpException) {

                val r = leerErrorJson<DetalleSalaResponse>(e)
                vista.mostrarError(r?.mensaje ?: "No se pudo cargar la sala")
            } catch (e: Exception) {
                vista.mostrarError("Sin conexión con el servidor: ${e.message}")
            } finally {
                vista.mostrarCargando(false)
            }
        }
    }

    // Se llama cuando el usuario elige un día en el calendario
    fun cambiarFecha(nuevaFecha: String) {
        val s = sala ?: return
        fecha = nuevaFecha
        limpiarSeleccion()
        vista.mostrarCargando(true)
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) { api.horariosDisponibles(s.idSala, nuevaFecha) }
                if (r.ok) {
                    horas = r.horas ?: emptyList()
                    vista.mostrarHoras(horas, r.mensaje)
                } else {
                    errorHoras(r.mensaje)
                }
            } catch (e: HttpException) {
                errorHoras(leerErrorJson<HorariosResponse>(e)?.mensaje ?: "")
            } catch (e: Exception) {
                vista.mostrarError("Sin conexión con el servidor: ${e.message}")
            } finally {
                vista.mostrarCargando(false)
            }
        }
    }

    // 1er toque = inicio, 2do toque = fin. Tocar la misma la quita.
    fun seleccionarHora(hora: Cls_HoraDisponible) {
        if (!hora.libre) {
            vista.mostrarError("Esa hora ya está ocupada")
            return
        }
        val h = hora.hora
        val i = inicio
        val f = fin
        when {
            i == null -> { inicio = h; fin = h }
            i == f && h == i -> { limpiarSeleccion(); return }
            i == f && h > i && rangoLibre(i, h) -> fin = h
            i == f && h < i && rangoLibre(h, i) -> inicio = h
            else -> { inicio = h; fin = h }
        }
        avisarSeleccion()
    }

    fun reservar() {
        val s = sala
        val d = fecha
        val i = inicio
        val f = fin
        when {
            s == null -> vista.mostrarError("Espera a que cargue la sala")
            d == null -> vista.mostrarError("Primero elige el día")
            i == null || f == null -> vista.mostrarError("Elige las horas que quieres")
            (f + 1 - i) < s.horasMinimas -> vista.mostrarError("La reserva mínima es de ${s.horasMinimas} horas")
            else -> vista.irAConfirmar(
                Cls_ModeloReservar(
                    idSala = s.idSala,
                    nombreSala = s.nombre,
                    fecha = d,
                    horaInicio = i,
                    horaFin = f + 1,
                    precioHora = s.precioHora,
                    precioDia = s.precioDia,
                    descuentoPct = s.promocion?.descuento ?: 0.0,
                    horaApertura = s.horaApertura,
                    horaCierre = s.horaCierre
                )
            )
        }
    }

    fun destruir() {
        scope.cancel()
    }

    private fun errorHoras(mensaje: String) {
        horas = emptyList()
        vista.mostrarHoras(horas, "")
        vista.mostrarError(mensaje.ifEmpty { "No se pudieron cargar los horarios" })
    }

    private fun rangoLibre(desde: Int, hasta: Int): Boolean =
        (desde..hasta).all { h -> horas.firstOrNull { it.hora == h }?.libre == true }

    private fun limpiarSeleccion() {
        inicio = null
        fin = null
        avisarSeleccion()
    }

    private fun avisarSeleccion() {
        val i = inicio
        val f = fin
        val texto = if (i == null || f == null) {
            "Toca la hora de inicio y luego la hora final"
        } else {
            String.format("De %02d:00 a %02d:00 (%d h)", i, f + 1, f + 1 - i)
        }
        vista.mostrarSeleccion(i, f, texto)
    }
}

// Cuando PHP responde 400/404/409, Retrofit lanza HttpException y el JSON
// con el mensaje viene en errorBody. La usan los dos presentadores.
val jsonLuis = Json { ignoreUnknownKeys = true }

inline fun <reified T> leerErrorJson(e: HttpException): T? = try {
    val texto = e.response()?.errorBody()?.string()
    if (texto.isNullOrEmpty()) null else jsonLuis.decodeFromString<T>(texto)
} catch (ex: Exception) {
    null
}