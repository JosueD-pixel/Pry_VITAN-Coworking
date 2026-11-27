<?php
// =====================================================================
//  RESPONSABLE: Luis
//  GET horarios_disponibles.php?id_sala=1&fecha=2026-11-28
//  Devuelve las horas de 9:00 a 22:00 de ese día y si están libres.
//  Cada hora es un bloque de 1 h: "10:00" = de 10:00 a 11:00.
//  Tablas: reservas, horarios (no cuentan las reservas canceladas)
// =====================================================================
require __DIR__ . '/api_comun.php';

$idSala = filter_input(INPUT_GET, 'id_sala', FILTER_VALIDATE_INT);
$fecha  = $_GET['fecha'] ?? '';

if (!$idSala) {
    responder(['ok' => false, 'mensaje' => 'Falta id_sala'], 400);
}
$d = DateTime::createFromFormat('Y-m-d', $fecha);
if (!$d || $d->format('Y-m-d') !== $fecha) {
    responder(['ok' => false, 'mensaje' => 'La fecha debe ir como AAAA-MM-DD'], 400);
}
$hoy = date('Y-m-d');
if ($fecha < $hoy) {
    responder(['ok' => false, 'mensaje' => 'No puedes reservar un día que ya pasó'], 400);
}

// Horarios ocupados de esa sala ese día
$st = $pdo->prepare(
    "SELECT h.hora_inicio, h.hora_fin
       FROM horarios h
       JOIN reservas r ON r.id_reserva = h.id_reserva
      WHERE r.id_sala = ? AND h.fecha = ? AND r.estado <> 'cancelada'"
);
$st->execute([$idSala, $fecha]);
$ocupados = $st->fetchAll();

$horaActual = (int) date('G');
$horas = [];
$libres = 0;

for ($h = HORA_APERTURA; $h < HORA_CIERRE; $h++) {
    $inicio = sprintf('%02d:00:00', $h);
    $fin    = sprintf('%02d:00:00', $h + 1);

    $libre = true;
    foreach ($ocupados as $o) {
        // se enciman si empieza antes de que termine el otro y termina después de que empieza
        if ($o['hora_inicio'] < $fin && $o['hora_fin'] > $inicio) {
            $libre = false;
            break;
        }
    }
    // Si es hoy, las horas que ya pasaron (o la que va corriendo) no se pueden elegir
    if ($fecha === $hoy && $h <= $horaActual) {
        $libre = false;
    }
    if ($libre) {
        $libres++;
    }
    $horas[] = [
        'hora'   => $h,
        'inicio' => substr($inicio, 0, 5),
        'fin'    => substr($fin, 0, 5),
        'libre'  => $libre,
    ];
}

responder([
    'ok'           => true,
    'mensaje'      => $libres > 0 ? 'OK' : 'No hay horas libres ese día',
    'fecha'        => $fecha,
    'horas_libres' => $libres,
    'horas'        => $horas,
]);
