<?php

$host = "localhost";
$db   = "vitan_coworking";
$user = "root"; // Cambia esto si tu MySQL tiene otro usuario
$pass = "";     // Cambia esto si tu MySQL tiene contraseña

try {
    // Conexión con charset utf8mb4 para tildes y ñ
    $pdo = new PDO("mysql:host=$host;dbname=$db;charset=utf8mb4", $user, $pass);
    $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
    // Lanzar excepciones si hay error en los queries
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    // Devolver los datos como array asociativo
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    http_response_code(500);
    header("Content-Type: application/json; charset=UTF-8");
    echo json_encode([
        "exito"   => false,
        "mensaje" => "No se pudo conectar con la base de datos"
    ], JSON_UNESCAPED_UNICODE);
    exit;
}
?>
