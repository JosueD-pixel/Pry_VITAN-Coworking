package com.example.pry_vitan_coworking.Presentador

import com.example.pry_vitan_coworking.Modelo.Cls_ModeloAdminUsuarios
import com.example.pry_vitan_coworking.Modelo.Cls_Usuario
import com.example.pry_vitan_coworking.Vista.AdminUsuariosActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.Normalizer

class Cls_AdminUsuariosPresentador(private val view: AdminUsuariosActivity) {
    private val modelo = Cls_ModeloAdminUsuarios()

    private var usuarios: List<Cls_Usuario> = emptyList() // lista completa del tipo elegido
    private var busqueda = ""                                   // lo que está escrito en el buscador

    // id_rol: 1 administrador, 2 cliente, 3 empresa o 0 para Todos
    fun cargarUsuarios(id_rol: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            view.mostrarCargando(true)
            try {
                usuarios = withContext(Dispatchers.IO) {
                    modelo.obtenerUsuarios(id_rol)
                }
                // Si ya había algo escrito en el buscador, se vuelve a aplicar
                aplicarBusqueda()
            } catch (e: IOException) {
                view.mostrarError("Revisa tu conexión a internet")
            } catch (e: Exception) {
                view.mostrarError("No se pudieron cargar los usuarios")
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
            usuarios
        } else {
            usuarios.filter {
                val nombreCompleto = listOfNotNull(it.nombre, it.ap, it.am).joinToString(" ")
                normalizar(nombreCompleto).contains(texto) || normalizar(it.correo).contains(texto)
            }
        }

        if (lista.isEmpty()) {
            view.mostrarSinUsuarios()
        } else {
            view.mostrarUsuarios(lista)
        }
    }

    // Minúsculas y sin acentos: "Martínez" se encuentra escribiendo "martinez"
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD).replace(Regex("""\p{Mn}+"""), "")
}
