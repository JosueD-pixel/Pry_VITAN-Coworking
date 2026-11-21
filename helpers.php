<?php
// Funciones compartidas por todos los endpoints.
// conexion.php vive en ../config/ (mobil_vitan/config/conexion.php)
require_once __DIR__ . '/../config/conexion.php';

header("Content-Type: application/json; charset=utf-8");

// Días mínimos de anticipación para cancelar SIN penalización.
// Si se cancela con menos días, la penalización es lo ya pagado (anticipo).
const DIAS_SIN_PENALIZACION = 5;

const ESTADOS_RESERVA = ['pendiente', 'confirmada', 'finalizada', 'cancelada'];

class ReglaNegocio extends Exception {}

function responder(bool $exito, string $mensaje, array $extra = [], int $codigo = 200): void {
    http_response_code($codigo);
    echo json_encode(
        array_merge(["exito" => $exito, "mensaje" => $mensaje], $extra),
        JSON_UNESCAPED_UNICODE
    );
    exit;
}

function exigir_metodo(string $metodo): void {
    if ($_SERVER['REQUEST_METHOD'] !== $metodo) {
        responder(false, "Método no permitido. Usa $metodo.", [], 405);
    }
}

// Junta GET, POST y cuerpo JSON (Retrofit / Volley suelen mandar JSON)
function entrada(): array {
    $json = json_decode(file_get_contents('php://input'), true);
    return array_merge($_GET, $_POST, is_array($json) ? $json : []);
}

// Identifica al usuario que hace la petición.
// MVP: llega id_usuario en la petición. Más adelante se cambia por token/sesión
// y solo se modifica esta función.
function usuario_actual(PDO $pdo, array $datos): array {
    $id = (int)($datos['id_usuario'] ?? 0);
    if ($id <= 0) {
        responder(false, "Falta id_usuario.", [], 401);
    }
    $st = $pdo->prepare(
        "SELECT u.id_usuario, r.rol
           FROM usuarios u JOIN roles r ON r.id_rol = u.id_rol
          WHERE u.id_usuario = :id"
    );
    $st->execute([':id' => $id]);
    $u = $st->fetch();
    if (!$u) {
        responder(false, "Usuario no encontrado.", [], 401);
    }
    return $u;
}

function exigir_admin(array $usuario): void {
    if ($usuario['rol'] !== 'administrador') {
        responder(false, "Solo el administrador puede consultar esto.", [], 403);
    }
}

// Paginación: ?limite=20&pagina=1
function paginacion(array $datos): array {
    $limite = min(max((int)($datos['limite'] ?? 20), 1), 100);
    $pagina = max((int)($datos['pagina'] ?? 1), 1);
    return [$limite, ($pagina - 1) * $limite];
}

// Consulta de reservas con sala, cliente, pagos, cancelación y horarios.
function listar_reservas(PDO $pdo, array $condiciones, array $params, int $limite, int $offset): array {
    $where = $condiciones ? 'WHERE ' . implode(' AND ', $condiciones) : '';

    $sql = "SELECT r.id_reserva, r.fecha AS fecha_reserva, r.estado, r.modalidad, r.motivo,
                   s.id_sala, s.nombre AS sala,
                   u.id_usuario, CONCAT_WS(' ', u.nombre, u.ap, u.am) AS cliente, ro.rol,
                   (SELECT COALESCE(SUM(p.monto), 0)
                      FROM pagos p WHERE p.id_reserva = r.id_reserva) AS total_pagado,
                   c.motivo AS motivo_cancelacion, c.penalizacion
              FROM reservas r
              JOIN salas s     ON s.id_sala = r.id_sala
              JOIN usuarios u  ON u.id_usuario = r.id_usuario
              JOIN roles ro    ON ro.id_rol = u.id_rol
              LEFT JOIN cancelaciones c ON c.id_reserva = r.id_reserva
              $where
              ORDER BY r.id_reserva DESC
              LIMIT :lim OFFSET :off";

    $st = $pdo->prepare($sql);
    foreach ($params as $k => $v) {
        $st->bindValue($k, $v);
    }
    $st->bindValue(':lim', $limite, PDO::PARAM_INT);
    $st->bindValue(':off', $offset, PDO::PARAM_INT);
    $st->execute();
    $reservas = $st->fetchAll();

    if (!$reservas) {
        return [];
    }

    // Horarios de todas las reservas encontradas, en una sola consulta
    $ids = array_column($reservas, 'id_reserva');
    $marcas = implode(',', array_fill(0, count($ids), '?'));
    $sh = $pdo->prepare(
        "SELECT id_reserva, fecha, hora_inicio, hora_fin
           FROM horarios WHERE id_reserva IN ($marcas)
          ORDER BY fecha, hora_inicio"
    );
    $sh->execute($ids);
    $horarios = [];
    foreach ($sh->fetchAll() as $h) {
        $id = $h['id_reserva'];
        unset($h['id_reserva']);
        $horarios[$id][] = $h;
    }

    foreach ($reservas as &$r) {
        $r['total_pagado'] = (float)$r['total_pagado'];
        $r['penalizacion'] = $r['penalizacion'] !== null ? (float)$r['penalizacion'] : null;
        $r['horarios']     = $horarios[$r['id_reserva']] ?? [];
    }
    return $reservas;
}
