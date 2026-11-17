<?php

// Regla del negocio (Anexo A): igual que en la app y en registro.php
const MIN_CONTRASENA = 8;
const MAX_CONTRASENA = 72; // bcrypt solo usa los primeros 72 bytes

// 1. Header JSON y conexión
header("Content-Type: application/json; charset=UTF-8");
require_once "conexion.php";

// 2. Solo se acepta POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "Método no permitido, use POST"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

// 3. Leer el cuerpo JSON enviado por Retrofit desde Android
//    (si llega como formulario, se usa $_POST)
$esJson = stripos($_SERVER['CONTENT_TYPE'] ?? '', 'application/json') !== false;
if ($esJson) {
    $jsonInput = file_get_contents("php://input");
    $data = json_decode($jsonInput, true);
    if (!is_array($data)) {
        echo json_encode([
            "exito"   => false,
            "mensaje" => "El cuerpo de la petición no es un JSON válido (debe ir en UTF-8)"
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
} else {
    $data = $_POST;
}

// 4. Sacar los datos (las contraseñas NO se recortan con trim)
$id_usuario        = isset($data['id_usuario']) ? intval($data['id_usuario']) : 0;
$contrasena_actual = isset($data['contrasena_actual']) && is_string($data['contrasena_actual']) ? $data['contrasena_actual'] : '';
$contrasena_nueva  = isset($data['contrasena_nueva'])  && is_string($data['contrasena_nueva'])  ? $data['contrasena_nueva']  : '';

// 5. Validaciones
if ($id_usuario <= 0 || $contrasena_actual === '' || $contrasena_nueva === '') {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "Debe enviar el usuario, la contraseña actual y la nueva"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

if (mb_strlen($contrasena_nueva, 'UTF-8') < MIN_CONTRASENA) {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "La nueva contraseña debe tener al menos " . MIN_CONTRASENA . " caracteres"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}
if (strlen($contrasena_nueva) > MAX_CONTRASENA) {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "La nueva contraseña admite máximo " . MAX_CONTRASENA . " caracteres"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

if ($contrasena_nueva === $contrasena_actual) {
    echo json_encode([
        "exito"   => false,
        "mensaje" => "La nueva contraseña debe ser distinta de la actual"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    // 6. Buscar la contraseña guardada (hash) del usuario
    $stmt = $pdo->prepare("SELECT contrasena FROM usuarios WHERE id_usuario = ?");
    $stmt->execute([$id_usuario]);
    $usuario = $stmt->fetch();

    if (!$usuario) {
        echo json_encode([
            "exito"   => false,
            "mensaje" => "El usuario no existe"
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }

    // 7. Comprobar la contraseña actual
    if (!password_verify($contrasena_actual, $usuario['contrasena'])) {
        echo json_encode([
            "exito"   => false,
            "mensaje" => "La contraseña actual es incorrecta"
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }

    // 8. Guardar la nueva contraseña cifrada
    $hash = password_hash($contrasena_nueva, PASSWORD_DEFAULT);
    $stmt = $pdo->prepare("UPDATE usuarios SET contrasena = ? WHERE id_usuario = ?");
    $stmt->execute([$hash, $id_usuario]);

    echo json_encode([
        "exito"   => true,
        "mensaje" => "Contraseña actualizada correctamente"
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    http_response_code(500);
    // Mensaje fijo sin exponer detalles internos de la base de datos
    echo json_encode([
        "exito"   => false,
        "mensaje" => "Ocurrió un error al cambiar la contraseña"
    ], JSON_UNESCAPED_UNICODE);
}
?>
