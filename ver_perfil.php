<?php

// 1. Header JSON y conexión
header("Content-Type: application/json; charset=UTF-8");
require_once "conexion.php";

// 2. Solo se acepta GET
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "Método no permitido, use GET"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// 3. Sacar y validar el id del usuario
$id_usuario = isset($_GET['id_usuario']) ? intval($_GET['id_usuario']) : 0;

if ($id_usuario <= 0) {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "El id del usuario no es válido"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    // 4. Buscar el usuario con el nombre de su rol (nunca se regresa la contraseña)
    $stmt = $pdo->prepare(
        "SELECT u.id_usuario, u.id_rol, r.rol, u.nombre, u.ap, u.am,
                u.cp, u.calle, u.ciudad, u.colonia, u.correo, u.telefono, u.favorito
           FROM usuarios u
           INNER JOIN roles r ON r.id_rol = u.id_rol
          WHERE u.id_usuario = ?"
    );
    $stmt->execute([$id_usuario]);
    $perfil = $stmt->fetch();

    if (!$perfil) {
        echo json_encode([
            "exito"   => false,
            "mensaje" => "El usuario no existe"
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }

    // 5. Casteo de tipos para que coincidan con la data class de Kotlin
    $perfil['id_usuario'] = (int)$perfil['id_usuario'];
    $perfil['id_rol']     = (int)$perfil['id_rol'];
    $perfil['favorito']   = (bool)$perfil['favorito'];

    // 6. Respuesta
    echo json_encode([
        "exito"   => true,
        "mensaje" => "Perfil encontrado",
        "perfil"  => $perfil
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    http_response_code(500);
    // Mensaje fijo sin exponer detalles internos de la base de datos
    echo json_encode([
        "exito"   => false,
        "mensaje" => "Ocurrió un error al consultar el perfil"
    ], JSON_UNESCAPED_UNICODE);
}
?>
