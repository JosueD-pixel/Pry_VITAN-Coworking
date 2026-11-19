package com.example.pry_vitan_coworking.Modelo

class clsCancelacionesModelo {
    suspend fun obtenerCancelaciones(): List<dataCancelacion> {
        return RetrofitCliente.apiService.obtenerCancelaciones()
    }
}