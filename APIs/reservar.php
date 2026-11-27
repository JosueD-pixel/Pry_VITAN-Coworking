<?php
// =====================================================================
//  RESPONSABLE: Luis
//  POST reservar.php   (cuerpo JSON)
//  {
//    "id_usuario": 3, "id_sala": 2, "fecha": "2026-11-28",
//    "hora_inicio": "10:00", "hora_fin": "13:00", "motivo": "Junta"
//  }
//  Crea la reserva (estado pendiente) y su horario. Valida:
//   - horario de 9:00 a 22:00 y mínimo 2 horas
//   - que el día no haya pasado
//   - que nadie más tenga la sala a esas horas
//  Calcula el total con la promoción activa y el anticipo del 50 %.
//  Tablas: reservas, horarios, salas, promociones
// =====================================================================
require __DIR__ . '/api_comun.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    responder(['ok' => false, 'mensaje' => 'Usa POST'], 405);
}

$datos = json_decode(file_get_contents('php://input'), true);
if (!is_array($datos)) {
    $datos = $_POST;   // por si se manda como formulario
}

$idUsuario  = filter_var($datos['id_usuario'] ?? null, FILTER_VALIDATE_INT);
$idSala     = filter_var($datos['id_sala'] ?? null, FILTER_VALIDATE_INT);
$fecha      = trim($datos['fecha'] ?? '');
$horaInicio = trim($datos['hora_inicio'] ?? '');
$horaFin    = trim($datos['hora_fin'] ?? '');
$motivo     = trim($datos['motivo'] ?? '');

// ---- Validaciones ---------------------------------------------------
if (!$idUsuario || !$idSala) {
    responder(['ok' => false, 'mensaje' => 'Faltan id_usuario o id_sala'], 400);
}
$d = DateTime::createFromFormat('Y-m-d', $fecha);
if (!$d || $d->format('Y-m-d') !== $fecha) {
    responder(['ok' => false, 'mensaje' => 'La fecha debe ir como AAAA-MM-DD'], 400);
}
if (!preg_match('/^\d{2}:00$/', $horaInicio) || !preg_match('/^\d{2}:00$/', $horaFin)) {
    responder(['ok' => false, 'mensaje' => 'Las horas deben ir en punto, como 10:00'], 400);
}

$hIni  = (int) substr($horaInicio, 0, 2);
$hFin  = (int) substr($horaFin, 0, 2);
$horas = $hFin - $hIni;

if ($hIni < HORA_APERTURA || $hFin > HORA_CIERRE || $horas <= 0) {
    responder(['ok' => false, 'mensaje' => 'El horario es de 9:00 a 22:00'], 400);
}
if ($horas < HORAS_MINIMAS) {
    responder(['ok' => false, 'mensaje' => 'La reserva mínima es de 2 horas'], 400);
}
$hoy = date('Y-m-d');
if ($fecha < $hoy || ($fecha === $hoy && $hIni <= (int) date('G'))) {
    responder(['ok' => false, 'mensaje' => 'Ese horario ya pasó'], 400);
}
if (mb_strlen($motivo) > 255) {
    responder(['ok' => false, 'mensaje' => 'El motivo es muy largo (máximo 255 letras)'], 400);
}

// El usuario debe existir y ser cliente (2) o empresa (3)
$st = $pdo->prepare("SELECT id_rol FROM usuarios WHERE id_usuario = ?");
$st->execute([$idUsuario]);
$rol = $st->fetchColumn();
if ($rol === false) {
    responder(['ok' => false, 'mensaje' => 'El usuario no existe'], 404);
}
if (!in_array((int) $rol, [2, 3], true)) {
    responder(['ok' => false, 'mensaje' => 'Solo clientes y empresas pueden reservar'], 403);
}

// La sala debe existir
$st = $pdo->prepare("SELECT nombre, precio_hora, precio_dia FROM salas WHERE id_sala = ?");
$st->execute([$idSala]);
$sala = $st->fetch();
if (!$sala) {
    responder(['ok' => false, 'mensaje' => 'La sala no existe'], 404);
}

// ---- Cotización (no se guarda, solo se calcula) ---------------------
// Si aparta todo el día (9:00 a 22:00) se cobra precio_dia; si no, por hora.
$todoElDia = ($hIni === HORA_APERTURA && $hFin === HORA_CIERRE);
$modalidad = $todoElDia ? 'dia' : 'hora';
$subtotal  = $todoElDia ? (float) $sala['precio_dia'] : $horas * (float) $sala['precio_hora'];

// La promoción la recibe quien reserva la sala en las fechas de la promoción (hoy)
$promo      = promocionActiva($pdo, $idSala, $hoy);
$porcentaje = $promo ? $promo['descuento'] : 0.0;
$descuento  = round($subtotal * $porcentaje / 100, 2);
$total      = round($subtotal - $descuento, 2);
$anticipo   = round($total * 0.5, 2);   // pregunta frecuente 2: anticipo del 50 %

$inicioSql = sprintf('%02d:00:00', $hIni);
$finSql    = sprintf('%02d:00:00', $hFin);

// ---- Guardar ----------------------------------------------------------
try {
    $pdo->beginTransaction();

    // Bloquea la sala mientras revisamos que nadie la tenga a esas horas
    $pdo->prepare("SELECT id_sala FROM salas WHERE id_sala = ? FOR UPDATE")->execute([$idSala]);

    $st = $pdo->prepare(
        "SELECT COUNT(*)
           FROM horarios h
           JOIN reservas r ON r.id_reserva = h.id_reserva
          WHERE r.id_sala = ? AND h.fecha = ? AND r.estado <> 'cancelada'
            AND h.hora_inicio < ? AND h.hora_fin > ?"
    );
    $st->execute([$idSala, $fecha, $finSql, $inicioSql]);
    if ((int) $st->fetchColumn() > 0) {
        $pdo->rollBack();
        responder(['ok' => false, 'mensaje' => 'Alguien ya apartó la sala en ese horario. Elige otras horas.'], 409);
    }

    // reservas.fecha = el día en que se hizo la reserva (hoy)
    // La tabla reservas no tiene columna "motivo". Si ya corriste el ALTER del
    // README (agregar_motivo.sql) se guarda; si no, simplemente no se guarda.
    $tieneMotivo = (bool) $pdo->query("SHOW COLUMNS FROM reservas LIKE 'motivo'")->fetch();

    if ($tieneMotivo) {
        $st = $pdo->prepare(
            "INSERT INTO reservas (id_usuario, id_sala, fecha, estado, modalidad, motivo)
             VALUES (?, ?, ?, 'pendiente', ?, ?)"
        );
        $st->execute([$idUsuario, $idSala, $hoy, $modalidad, $motivo !== '' ? $motivo : null]);
    } else {
        $st = $pdo->prepare(
            "INSERT INTO reservas (id_usuario, id_sala, fecha, estado, modalidad)
             VALUES (?, ?, ?, 'pendiente', ?)"
        );
        $st->execute([$idUsuario, $idSala, $hoy, $modalidad]);
    }
    $idReserva = (int) $pdo->lastInsertId();

    // horarios.fecha = el día en que se usa la sala
    $st = $pdo->prepare(
        "INSERT INTO horarios (id_reserva, fecha, hora_inicio, hora_fin) VALUES (?, ?, ?, ?)"
    );
    $st->execute([$idReserva, $fecha, $inicioSql, $finSql]);

    $pdo->commit();
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    responder(['ok' => false, 'mensaje' => 'No se pudo guardar la reserva'], 500);
}

responder([
    'ok'             => true,
    'mensaje'        => 'Reserva registrada. Paga el anticipo del 50 % para confirmarla.',
    'id_reserva'     => $idReserva,
    'estado'         => 'pendiente',
    'modalidad'      => $modalidad,
    'sala'           => $sala['nombre'],
    'fecha'          => $fecha,
    'hora_inicio'    => substr($inicioSql, 0, 5),
    'hora_fin'       => substr($finSql, 0, 5),
    'horas'          => $horas,
    'subtotal'       => $subtotal,
    'descuento_pct'  => $porcentaje,
    'descuento'      => $descuento,
    'total'          => $total,
    'anticipo'       => $anticipo,
    'motivo_guardado'=> $tieneMotivo,
]);
