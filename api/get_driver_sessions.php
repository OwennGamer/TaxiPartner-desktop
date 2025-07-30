<?php
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/db.php';

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    http_response_code(401);
    echo json_encode(['status' => 'error', 'message' => 'Brak ważnego tokena']);
    exit;
}
if ($decoded->role !== 'admin' && $decoded->role !== 'flotowiec') {
    http_response_code(403);
    echo json_encode(['status' => 'error', 'message' => 'Brak uprawnień']);
    exit;
}

$driverId  = $_GET['driver_id']  ?? '';
$startDate = $_GET['start_date'] ?? '';
$endDate   = $_GET['end_date']   ?? '';
if (!$driverId || !$startDate || !$endDate) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych parametrów']);
    exit;
}

$format = 'Y-m-d';
$startObj = DateTime::createFromFormat($format, $startDate);
$endObj   = DateTime::createFromFormat($format, $endDate);
if (!$startObj || $startObj->format($format) !== $startDate ||
    !$endObj || $endObj->format($format) !== $endDate ||
    $startObj > $endObj) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowe daty']);
    exit;
}

try {
    $stmt = $pdo->prepare(
        "SELECT DATE_FORMAT(start_time,'%Y-%m-%d %H:%i:%s') AS start_time,\n" .
        "       DATE_FORMAT(end_time,'%Y-%m-%d %H:%i:%s') AS end_time,\n" .
        "       start_odometer,\n" .
        "       end_odometer\n" .
        "  FROM work_sessions\n" .
        " WHERE driver_id = ?\n" .
        "   AND DATE(start_time) BETWEEN ? AND ?\n" .
        "   AND end_time IS NOT NULL\n" .
        "   AND end_odometer IS NOT NULL\n" .
        " ORDER BY start_time"
    );
    $stmt->execute([$driverId, $startDate, $endDate]);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(['status' => 'success', 'data' => $rows]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
