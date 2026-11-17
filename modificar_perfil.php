<?php

// 1. Header JSON y conexión
header("Content-Type: application/json; charset=UTF-8");
require_once "conexion.php";

// Responder un error y terminar. Como en las APIs del profe, los errores
// de validación salen con HTTP 200 para que Retrofit lea "mensaje"
// (con 4xx lanzaría una excepción); solo el error del servidor usa 500.
function responderError($mensaje, $campo = null) {
    $respuesta = [
        "exito"   => false,
        "mensaje" => $mensaje
    ];
    if ($campo !== null) {
        $respuesta["campo"] = $campo;
    }
    echo json_encode($respuesta, JSON_UNESCAPED_UNICODE);
    exit;
}

// 2. Solo se acepta POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    responderError("Método no permitido, use POST");
}

// 3. Leer el cuerpo JSON enviado por Retrofit desde Android
//    (si llega como formulario, se usa $_POST)
$esJson = stripos($_SERVER['CONTENT_TYPE'] ?? '', 'application/json') !== false;
if ($esJson) {
    $data = json_decode(file_get_contents("php://input"), true);
    if (!is_array($data)) {
        responderError("El cuerpo de la petición no es un JSON válido (debe ir en UTF-8)");
    }
} else {
    $data = $_POST;
}

// 4. Sacar el id y solo los campos que se pueden modificar en Mi perfil
$id_usuario = isset($data['id_usuario']) ? intval($data['id_usuario']) : 0;
if ($id_usuario <= 0) {
    responderError("El id del usuario no es válido");
}

$permitidos = ['telefono', 'calle', 'cp', 'colonia', 'ciudad'];
$cambios = [];
foreach ($permitidos as $campo) {
    if (array_key_exists($campo, $data)) {
        $valor = $data[$campo];
        if ($valor !== null && !is_scalar($valor)) {
            responderError("El campo $campo no es válido", $campo);
        }
        $valor = trim((string)$valor);
        $cambios[$campo] = $valor === '' ? null : $valor;
    }
}

if (empty($cambios)) {
    responderError("No se envió ningún dato para guardar");
}

// 5. Validaciones (se responde con el primer campo que falle)
foreach ($cambios as $campo => $valor) {
    if ($valor !== null && !mb_check_encoding($valor, 'UTF-8')) {
        responderError("El campo $campo tiene caracteres no válidos (debe ir en UTF-8)", $campo);
    }
}

if (isset($cambios['telefono'])) {
    // En la pantalla se ve "771 123 4567": se guardan solo los dígitos
    $cambios['telefono'] = preg_replace('/[\s\-()]/', '', $cambios['telefono']);
    if (!preg_match('/^[0-9]{10}$/', $cambios['telefono'])) {
        responderError("El teléfono debe tener 10 dígitos", "telefono");
    }
}
if (isset($cambios['cp']) && !preg_match('/^[0-9]{5}$/', $cambios['cp'])) {
    responderError("El código postal debe tener 5 dígitos", "cp");
}

// Largo máximo de cada columna de la tabla usuarios
$maximos = ['calle' => 100, 'colonia' => 80, 'ciudad' => 60];
foreach ($maximos as $campo => $max) {
    if (isset($cambios[$campo]) && mb_strlen($cambios[$campo], 'UTF-8') > $max) {
        responderError("El campo $campo admite máximo $max caracteres", $campo);
    }
}

try {
    // 6. Validar que el usuario exista
    $stmt = $pdo->prepare("SELECT id_usuario FROM usuarios WHERE id_usuario = ?");
    $stmt->execute([$id_usuario]);
    if (!$stmt->fetch()) {
        responderError("El usuario no existe");
    }

    // 7. Actualizar solo los campos enviados
    //    (los nombres de columna salen de la lista $permitidos, no del usuario)
    $sets = [];
    $valores = [];
    foreach ($cambios as $campo => $valor) {
        $sets[] = "$campo = ?";
        $valores[] = $valor;
    }
    $valores[] = $id_usuario;

    $stmt = $pdo->prepare("UPDATE usuarios SET " . implode(", ", $sets) . " WHERE id_usuario = ?");
    $stmt->execute($valores);

    // 8. Regresar el perfil ya actualizado para refrescar la pantalla
    $stmt = $pdo->prepare(
        "SELECT u.id_usuario, u.id_rol, r.rol, u.nombre, u.ap, u.am,
                u.cp, u.calle, u.ciudad, u.colonia, u.correo, u.telefono, u.favorito
           FROM usuarios u
           INNER JOIN roles r ON r.id_rol = u.id_rol
          WHERE u.id_usuario = ?"
    );
    $stmt->execute([$id_usuario]);
    $perfil = $stmt->fetch();

    $perfil['id_usuario'] = (int)$perfil['id_usuario'];
    $perfil['id_rol']     = (int)$perfil['id_rol'];
    $perfil['favorito']   = (bool)$perfil['favorito'];

    echo json_encode([
        "exito"   => true,
        "mensaje" => "Datos guardados",
        "perfil"  => $perfil
    ], JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    http_response_code(500);
    // Mensaje fijo sin exponer detalles internos de la base de datos
    responderError("Ocurrió un error al guardar los datos");
}
?>
