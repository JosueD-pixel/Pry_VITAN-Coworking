package com.example.pry_vitan_coworking.Modelo

// Pide la lista de usuarios a listar_usuarios.php por medio de Retrofit.
// El filtro por tipo lo hace la API; la búsqueda por nombre o correo la hace
// el presentador sobre la lista que regresa este modelo.
class Cls_ModeloAdminUsuarios {

    // id_rol: 1 administrador, 2 cliente, 3 empresa o 0 para todos
    suspend fun obtenerUsuarios(id_rol: Int): List<UsuarioResponse> {
        return RetrofitCliente.apiService.listarUsuarios(id_rol)
    }
}
