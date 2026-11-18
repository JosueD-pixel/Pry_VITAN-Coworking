package com.example.pry_vitan_coworking.Modelo

// Pide la lista de reservas a consultar_reservas.php por medio de Retrofit.
// El filtro de estado lo hace la API; la búsqueda por cliente o sala la hace
// el presentador sobre la lista que regresa este modelo.
class Cls_ModeloAdminReservas {

    // estado: "pendiente", "confirmada", "finalizada", "cancelada" o "" para todas
    suspend fun obtenerReservas(estado: String): List<ReservaResponse> {
        return RetrofitCliente.apiService.consultarReservas(estado)
    }
}
