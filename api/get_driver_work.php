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

$role = strtolower($decoded->role ?? '');
if (!in_array($role, ['admin', 'administrator', 'flotowiec'], true)) {
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
        "SELECT DATE(start_time) AS work_date,\n" .
        "       SUM(TIMESTAMPDIFF(SECOND, start_time, end_time)) / 3600 AS hours,\n" .
        "       SUM(end_odometer - start_odometer) AS kilometers\n" .
        "  FROM work_sessions\n" .
        " WHERE driver_id = ?\n" .
        "   AND DATE(start_time) BETWEEN ? AND ?\n" .
        "   AND end_time IS NOT NULL\n" .
        "   AND end_odometer IS NOT NULL\n" .
        " GROUP BY work_date\n" .
        " ORDER BY work_date"
    );
    $stmt->execute([$driverId, $startDate, $endDate]);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $data = array_map(function($row) {
        return [
            'date'       => $row['work_date'],
            'hours'      => (float)$row['hours'],
            'kilometers' => (int)$row['kilometers']
        ];
    }, $rows);

    echo json_encode(['status' => 'success', 'data' => $data]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
