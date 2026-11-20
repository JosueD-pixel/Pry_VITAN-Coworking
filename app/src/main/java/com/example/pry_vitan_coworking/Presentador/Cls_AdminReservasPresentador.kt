package com.example.pry_vitan_coworking.Presentador

import com.example.pry_vitan_coworking.Modelo.Cls_ModeloAdminReservas
import com.example.pry_vitan_coworking.Modelo.Cls_Reserva
import com.example.pry_vitan_coworking.Vista.AdminReservasActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.Normalizer

class Cls_AdminReservasPresentador(private val view: AdminReservasActivity) {
    private val modelo = Cls_ModeloAdminReservas()

    private var reservas: List<Cls_Reserva> = emptyList() // lista completa del estado elegido
    private var busqueda = ""                                   // lo que está escrito en el buscador

    fun cargarReservas(estado: String) {
        CoroutineScope(Dispatchers.Main).launch {
            view.mostrarCargando(true)
            try {
                reservas = withContext(Dispatchers.IO) {
                    modelo.obtenerReservas(estado)
                }
                // Si ya había algo escrito en el buscador, se vuelve a aplicar
                aplicarBusqueda()
            } catch (e: IOException) {
                view.mostrarError("Revisa tu conexión a internet")
            } catch (e: Exception) {
                view.mostrarError("No se pudieron cargar las reservas")
            } finally {
                view.mostrarCargando(false)
            }
        }
    }

    // Se llama cada vez que cambia el texto del buscador
    fun buscar(texto: String) {
        busqueda = texto
        aplicarBusqueda()
    }

    private fun aplicarBusqueda() {
        val texto = normalizar(busqueda)
        val lista = if (texto.isEmpty()) {
            reservas
        } else {
            reservas.filter { normalizar(it.cliente).contains(texto) || normalizar(it.sala).contains(texto) }
        }

        if (lista.isEmpty()) {
            view.mostrarSinReservas()
        } else {
            view.mostrarReservas(lista)
        }
    }

    // Minúsculas y sin acentos: "Hernández" se encuentra escribiendo "hernandez"
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD).replace(Regex("""\p{Mn}+"""), "")
}
