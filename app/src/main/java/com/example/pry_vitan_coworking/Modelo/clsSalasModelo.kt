package com.example.pry_vitan_coworking.Modelo

class clsSalasModelo {
    suspend fun obtenerTiposSala(): List<dataTipoSala> {
        return RetrofitCliente.apiService.obtenerTiposSala()
    }
    suspend fun obtenerSalas(id_tipo_sala: Int): List<dataSala> {
        return RetrofitCliente.apiService.obtenerSalas(id_tipo_sala)
    }
}