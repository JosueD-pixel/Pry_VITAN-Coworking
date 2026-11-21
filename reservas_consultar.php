<?php
// CLIENTE · Mis reservas · Consultar
// GET  reservas_consultar.php?id_usuario=3
// Opcionales: &estado=pendiente  &id_reserva=3  &limite=20  &pagina=1
require_once __DIR__ . '/helpers.php';

exigir_metodo('GET');
$datos   = entrada();
$usuario = usuario_actual($pdo, $datos);

$condiciones = ['r.id_usuario = :usuario'];
$params      = [':usuario' => $usuario['id_usuario']];

if (!empty($datos['estado'])) {
    if (!in_array($datos['estado'], ESTADOS_RESERVA, true)) {
        responder(false, "Estado no válido.", [], 400);
    }
    $condiciones[] = 'r.estado = :estado';
    $params[':estado'] = $datos['estado'];
}
if (!empty($datos['id_reserva'])) {
    $condiciones[] = 'r.id_reserva = :reserva';
    $params[':reserva'] = (int)$datos['id_reserva'];
}

[$limite, $offset] = paginacion($datos);

try {
    $reservas = listar_reservas($pdo, $condiciones, $params, $limite, $offset);
    responder(true, "Reservas consultadas.", ["total" => count($reservas), "reservas" => $reservas]);
} catch (PDOException $e) {
    responder(false, "No se pudieron consultar las reservas.", [], 500);
}
