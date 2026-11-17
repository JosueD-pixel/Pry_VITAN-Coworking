<?php
// CLIENTE · Mis reservas · Cancelar
// POST reservas_cancelar.php
// Cuerpo (JSON o formulario): { "id_usuario": 2, "id_reserva": 5, "motivo": "texto opcional" }
require_once __DIR__ . '/helpers.php';

exigir_metodo('POST');
$datos   = entrada();
$usuario = usuario_actual($pdo, $datos);

$idReserva = (int)($datos['id_reserva'] ?? 0);
$motivo    = trim((string)($datos['motivo'] ?? ''));

if ($idReserva <= 0) {
    responder(false, "Falta id_reserva.", [], 400);
}
if (mb_strlen($motivo) > 255) {
    responder(false, "El motivo no puede pasar de 255 caracteres.", [], 400);
}

try {
    $pdo->beginTransaction();

    // Bloquea la fila para que no se cancele dos veces al mismo tiempo
    $st = $pdo->prepare("SELECT id_reserva, id_usuario, estado FROM reservas WHERE id_reserva = :id FOR UPDATE");
    $st->execute([':id' => $idReserva]);
    $reserva = $st->fetch();

    if (!$reserva) {
        throw new ReglaNegocio("La reserva no existe.", 404);
    }
    if ((int)$reserva['id_usuario'] !== (int)$usuario['id_usuario']) {
        throw new ReglaNegocio("Esta reserva no te pertenece.", 403);
    }
    if (!in_array($reserva['estado'], ['pendiente', 'confirmada'], true)) {
        throw new ReglaNegocio("Una reserva '{$reserva['estado']}' no se puede cancelar.", 409);
    }

    // Primer día de uso de la sala
    $st = $pdo->prepare("SELECT MIN(fecha) FROM horarios WHERE id_reserva = :id");
    $st->execute([':id' => $idReserva]);
    $primerDia = $st->fetchColumn();

    $hoy = new DateTime('today');
    $dias = null;
    if ($primerDia) {
        $inicio = new DateTime($primerDia);
        if ($inicio < $hoy) {
            throw new ReglaNegocio("La reserva ya comenzó o ya pasó, no se puede cancelar.", 409);
        }
        $dias = (int)$hoy->diff($inicio)->days;
    }

    // Penalización: con menos de DIAS_SIN_PENALIZACION días se pierde lo pagado
    $st = $pdo->prepare("SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE id_reserva = :id");
    $st->execute([':id' => $idReserva]);
    $pagado = (float)$st->fetchColumn();

    $penalizacion = ($dias !== null && $dias < DIAS_SIN_PENALIZACION) ? $pagado : 0.00;

    $pdo->prepare("UPDATE reservas SET estado = 'cancelada' WHERE id_reserva = :id")
        ->execute([':id' => $idReserva]);

    $pdo->prepare(
        "INSERT INTO cancelaciones (id_reserva, motivo, penalizacion)
         VALUES (:id, :motivo, :pen)"
    )->execute([
        ':id'     => $idReserva,
        ':motivo' => $motivo !== '' ? $motivo : null,
        ':pen'    => $penalizacion,
    ]);

    $pdo->commit();

    responder(true, "Reserva cancelada correctamente.", [
        "id_reserva"   => $idReserva,
        "penalizacion" => $penalizacion,
        "total_pagado" => $pagado,
    ]);

} catch (ReglaNegocio $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    responder(false, $e->getMessage(), [], $e->getCode());
} catch (PDOException $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    responder(false, "No se pudo cancelar la reserva.", [], 500);
}
