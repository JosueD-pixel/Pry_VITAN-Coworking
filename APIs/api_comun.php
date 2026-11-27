<?php
// =====================================================================
//  APIs/api_comun.php · Lo que comparten las APIs de la app (JSON)
//  RESPONSABLE: Luis
//  Usa la conexión del proyecto: config/conexion.php
// =====================================================================

header('Content-Type: application/json; charset=utf-8');
date_default_timezone_set('America/Mexico_City');

// Regla de VITÁN (pregunta frecuente 4): de 9:00 a 22:00, mínimo 2 horas
const HORA_APERTURA = 9;
const HORA_CIERRE   = 22;
const HORAS_MINIMAS = 2;

function responder(array $datos, int $codigo = 200): void
{
    http_response_code($codigo);
    echo json_encode($datos, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

// ---- Conexión ---------------------------------------------------------
// Se carga config/conexion.php y se toma el objeto PDO que crea, se llame
// $pdo, $conexion, $conn o $db. Si ese archivo usa mysqli (no PDO), aquí
// se abre un PDO con los datos de XAMPP para que las APIs funcionen igual.
ob_start();                                   // por si conexion.php imprime algo
require_once __DIR__ . '/conexion.php';
ob_end_clean();

$pdo = $pdo ?? null;
if (!($pdo instanceof PDO)) {
    $pdo = null;
    foreach (['conexion', 'conn', 'db', 'cn'] as $nombre) {
        if (isset($$nombre) && $$nombre instanceof PDO) {
            $pdo = $$nombre;
            break;
        }
    }
}
try {
    if (!$pdo) {
        $pdo = new PDO('mysql:host=localhost;dbname=vitan_coworking;charset=utf8mb4', 'root', '');
    }
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    responder(['ok' => false, 'mensaje' => 'No se pudo conectar a la base de datos'], 500);
}

// URL de la raíz del proyecto, p. ej. http://10.0.2.2/vitan/ (para las fotos)
function urlBase(): string
{
    $carpeta = rtrim(str_replace('\\', '/', dirname(dirname($_SERVER['SCRIPT_NAME']))), '/');
    return 'http://' . $_SERVER['HTTP_HOST'] . $carpeta . '/';
}

// Convierte la ruta guardada en imagenes.img en URL completa. Busca el archivo
// en la raíz, en assets/ y en uploads/ (según dónde lo guarde el admin).
function urlArchivo(string $ruta): string
{
    $ruta = ltrim(str_replace('\\', '/', $ruta), '/');
    $raiz = dirname(__DIR__) . '/';
    foreach (['', 'assets/', 'uploads/'] as $prefijo) {
        if (is_file($raiz . $prefijo . $ruta)) {
            return urlBase() . $prefijo . $ruta;
        }
    }
    return urlBase() . $ruta;
}

// Promoción activa de una sala en una fecha (null si no hay)
function promocionActiva(PDO $pdo, int $idSala, string $fecha): ?array
{
    $st = $pdo->prepare(
        "SELECT id_promocion, descuento, fecha_inicio, fecha_fin
           FROM promociones
          WHERE id_sala = ? AND estado = 'activa'
            AND ? BETWEEN fecha_inicio AND fecha_fin
          ORDER BY descuento DESC
          LIMIT 1"
    );
    $st->execute([$idSala, $fecha]);
    $promo = $st->fetch();
    if (!$promo) {
        return null;
    }
    $promo['id_promocion'] = (int) $promo['id_promocion'];
    $promo['descuento']    = (float) $promo['descuento'];
    return $promo;
}
