package com.example.pry_vitan_coworking.Presentador

import com.example.pry_vitan_coworking.Modelo.clsSalasModelo
import com.example.pry_vitan_coworking.Modelo.dataSala
import com.example.pry_vitan_coworking.Modelo.dataTipoSala
import com.example.pry_vitan_coworking.Vista.SalasActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//C01 Salas: llena el Spinner de tipos y la lista de salas filtrada
class clsSalasPresentador(private val vista: SalasActivity) {
    private val modelo = clsSalasModelo()
    private var listaTipos: List<dataTipoSala> = emptyList()

    fun iniciar() {
        //se usa un scope simple para no bloquear la interfaz
        CoroutineScope(Dispatchers.Main).launch {
            try {
                //1 Cargar los tipos de sala en el spinner
                val tipos = withContext(Dispatchers.IO) {
                    modelo.obtenerTiposSala()
                }
                //Opcion para ver todas las salas (listar_salas con id_tipo_sala = 0)
                listaTipos = listOf(dataTipoSala(0, "Todas")) + tipos
                vista.mostrarTiposSala(listaTipos)
                //2 Al poner el adaptador, el Spinner selecciona "Todas" solo
                //  y llama a seleccionarTipo(0), que carga todas las salas
            } catch (e: Exception) {
                vista.mostrarError("Revisa tu conexión a internet")
            }
        }
    }

    fun seleccionarTipo(posicion: Int) {
        if (posicion >= 0 && posicion < listaTipos.size) {
            val tipo = listaTipos[posicion]
            //Si id_tipo_sala es 0 la API trae todas, si es >0 filtra por ese tipo
            cargarSalas(tipo.id_tipo_sala)
        }
    }

    private fun cargarSalas(id_tipo_sala: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val salas = withContext(Dispatchers.IO) {
                    modelo.obtenerSalas(id_tipo_sala)
                }
                if (salas.isEmpty()) {
                    vista.mostrarSinSalas()
                } else {
                    vista.mostrarSalas(salas)
                }
            } catch (e: Exception) {
                vista.mostrarError("Revisa tu conexión a internet")
            }
        }
    }

    //Al tocar una tarjeta se abre el detalle C02 (Luis Angel)
    fun seleccionarSala(sala: dataSala) {
        if (sala.id_sala <= 0) {
            vista.mostrarError("No se pudo identificar la sala")
            return
        }
        vista.abrirDetalleSala(sala.id_sala)
    }
}