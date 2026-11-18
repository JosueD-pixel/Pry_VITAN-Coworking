package com.example.pry_vitan_coworking.InterfaceApi

import com.example.pry_vitan_coworking.Modelo.dataCancelacion
import com.example.pry_vitan_coworking.Modelo.dataSala
import com.example.pry_vitan_coworking.Modelo.dataTipoSala

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Nathan - Salas

    //Lista de tipos de sala para el Spinner
    @GET("listar_tipos_sala.php")
    suspend fun obtenerTiposSala(): List<dataTipoSala>

    //Salas de un tipo (id_tipo_sala = 0 trae todas)
    @GET("listar_salas.php")
    suspend fun obtenerSalas(@Query("id_tipo_sala") id_tipo_sala: Int): List<dataSala>

    //Lista de cancelaciones
    @GET("consultar_cancelaciones.php")
    suspend fun obtenerCancelaciones(): List<dataCancelacion>
}