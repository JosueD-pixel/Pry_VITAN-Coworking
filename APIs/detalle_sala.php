<?php
// =====================================================================
//  RESPONSABLE: Luis
//  GET detalle_sala.php?id_sala=1
//  Devuelve la sala con sus fotos, mobiliario, promoción activa y video.
//  Tablas: salas, tipos_sala, imagenes, asigna, mobiliario, promociones
// =====================================================================
require __DIR__ . '/api_comun.php';

$idSala = filter_input(INPUT_GET, 'id_sala', FILTER_VALIDATE_INT);
if (!$idSala) {
    responder(['ok' => false, 'mensaje' => 'Falta id_sala'], 400);
}

// 1. Datos de la sala
$st = $pdo->prepare(
    "SELECT s.id_sala, s.nombre, t.nombre AS tipo, s.capacidad, s.descripcion,
            s.precio_hora, s.precio_dia
       FROM salas s
       JOIN tipos_sala t ON t.id_tipo_sala = s.id_tipo_sala
      WHERE s.id_sala = ?"
);
$st->execute([$idSala]);
$sala = $st->fetch();
if (!$sala) {
    responder(['ok' => false, 'mensaje' => 'La sala no existe'], 404);
}
$sala['id_sala']     = (int) $sala['id_sala'];
$sala['capacidad']   = (int) $sala['capacidad'];
$sala['precio_hora'] = (float) $sala['precio_hora'];
$sala['precio_dia']  = (float) $sala['precio_dia'];

$base = urlBase();

// 2. Fotos (tabla imagenes). Se manda la URL completa para que la app la cargue.
$st = $pdo->prepare("SELECT img FROM imagenes WHERE id_sala = ? ORDER BY id_imagen");
$st->execute([$idSala]);
$sala['imagenes'] = array_map(fn($f) => urlArchivo($f['img']), $st->fetchAll());

// 3. Mobiliario (tabla asigna + mobiliario)
$st = $pdo->prepare(
    "SELECT m.id_mobiliario, m.marca, m.nombre, m.descripcion
       FROM asigna a
       JOIN mobiliario m ON m.id_mobiliario = a.id_mobiliario
      WHERE a.id_sala = ?
      ORDER BY m.nombre"
);
$st->execute([$idSala]);
$mobiliario = $st->fetchAll();
foreach ($mobiliario as &$m) {
    $m['id_mobiliario'] = (int) $m['id_mobiliario'];
}
unset($m);
$sala['mobiliario'] = $mobiliario;

// 4. Promoción activa hoy
$sala['promocion'] = promocionActiva($pdo, $idSala, date('Y-m-d'));

// 5. Video. La BD no tiene columna de video, así que el video se busca
//    como archivo: uploads/videos/sala_<id>.mp4. Si no existe, va null
//    y la app esconde el reproductor.
$archivoVideo = 'uploads/videos/sala_' . $idSala . '.mp4';
$sala['video'] = is_file(dirname(__DIR__) . '/' . $archivoVideo) ? $base . $archivoVideo : null;

// Reglas para que la app las muestre
$sala['hora_apertura'] = HORA_APERTURA;
$sala['hora_cierre']   = HORA_CIERRE;
$sala['horas_minimas'] = HORAS_MINIMAS;

responder(['ok' => true, 'mensaje' => 'OK', 'sala' => $sala]);
