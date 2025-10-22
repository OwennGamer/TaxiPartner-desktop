<?php
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/db.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Niedozwolona metoda"]);
    exit;
}

$driverId = isset($_GET['driver_id']) ? trim((string)$_GET['driver_id']) : '';
$licensePlate = isset($_GET['license_plate']) ? trim((string)$_GET['license_plate']) : '';
$from = isset($_GET['from']) ? trim((string)$_GET['from']) : '';
$to = isset($_GET['to']) ? trim((string)$_GET['to']) : '';
$limitParam = isset($_GET['limit']) ? (int)$_GET['limit'] : 200;
$limit = max(1, min($limitParam, 2000));

function parseDateTime(?string $value): ?string
{
    if ($value === null || $value === '') {
        return null;
    }

    $patterns = [
        'Y-m-d\TH:i:sP',
        'Y-m-d H:i:s',
        'Y-m-d',
    ];

    foreach ($patterns as $pattern) {
        $dt = DateTime::createFromFormat($pattern, $value);
        if ($dt instanceof DateTime) {
            return $dt->format('Y-m-d H:i:s');
        }
    }

    $timestamp = strtotime($value);
    if ($timestamp !== false) {
        return date('Y-m-d H:i:s', $timestamp);
    }

    return null;
}

$conditions = [];
$params = [];

if ($driverId !== '') {
    $conditions[] = 'driver_id = ?';
    $params[] = $driverId;
}

if ($licensePlate !== '') {
    $conditions[] = 'license_plate = ?';
    $params[] = $licensePlate;
}

$fromDate = parseDateTime($from);
if ($from !== '' && !$fromDate) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nieprawidłowa data początkowa"]);
    exit;
}
if ($fromDate) {
    $conditions[] = 'created_at >= ?';
    $params[] = $fromDate;
}

$toDate = parseDateTime($to);
if ($to !== '' && !$toDate) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nieprawidłowa data końcowa"]);
    exit;
}
if ($toDate) {
    $conditions[] = 'created_at <= ?';
    $params[] = $toDate;
}

$query = 'SELECT id, source, level, summary, message, stacktrace, driver_id, license_plate, app_version, device_id, metadata, created_at '
    . 'FROM app_error_logs';
if (!empty($conditions)) {
    $query .= ' WHERE ' . implode(' AND ', $conditions);
}
$query .= ' ORDER BY created_at DESC LIMIT ' . $limit;

try {
    $stmt = $pdo->prepare($query);
    $stmt->execute($params);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($rows as &$row) {
        if (!empty($row['metadata'])) {
            $decoded = json_decode($row['metadata'], true);
            if (json_last_error() === JSON_ERROR_NONE) {
                $row['metadata'] = $decoded;
            }
        }
    }

    echo json_encode([
        "status" => "success",
        "data" => $rows,
    ]);
} catch (PDOException $e) {
    error_log('get_error_logs.php failed: ' . $e->getMessage());
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
