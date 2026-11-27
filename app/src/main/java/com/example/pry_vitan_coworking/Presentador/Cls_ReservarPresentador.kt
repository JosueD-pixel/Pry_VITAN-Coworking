package com.example.pry_vitan_coworking.Presentador

import com.example.pry_vitan_coworking.Modelo.Cls_ModeloReservar
import com.example.pry_vitan_coworking.Modelo.ReservarResponse
import com.example.pry_vitan_coworking.Modelo.RetrofitCliente
import com.example.pry_vitan_coworking.Vista.ConfirmarReservaActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

// Confirmar reserva: valida y manda la reserva a reservar.php
class Cls_ReservarPresentador(private val vista: ConfirmarReservaActivity) {

    private val api = RetrofitCliente.apiService
    private val scope = CoroutineScope(Dispatchers.Main)

    fun confirmar(reserva: Cls_ModeloReservar, idUsuario: Int, motivo: String) {
        // 1 Validar antes de llamar a la API
        if (idUsuario <= 0) {
            vista.mostrarError("Inicia sesión para reservar")
            return
        }
        if (motivo.length > 255) {
            vista.mostrarError("El motivo es muy largo (máximo 255 letras)")
            return
        }

        // 2 Llamar a la API sin bloquear la pantalla
        vista.mostrarCargando(true)
        scope.launch {
            try {
                val r = withContext(Dispatchers.IO) {
                    api.reservar(reserva.aRequest(idUsuario, motivo.trim()))
                }
                if (r.ok) vista.reservaExitosa(r)
                else vista.mostrarError(r.mensaje.ifEmpty { "No se pudo hacer la reserva" })
            } catch (e: HttpException) {
                // 409 = ese horario ya lo apartó alguien más, etc.
                val r = leerErrorJson<ReservarResponse>(e)
                vista.mostrarError(r?.mensaje ?: "No se pudo hacer la reserva")
            } catch (e: Exception) {
                vista.mostrarError("Sin conexión con el servidor: ${e.message}")
            } finally {
                vista.mostrarCargando(false)
            }
        }
    }

    fun destruir() {
        scope.cancel()
    }
}