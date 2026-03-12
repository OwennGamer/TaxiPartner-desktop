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

$startDate = $_GET['start_date'] ?? '';
$endDate = $_GET['end_date'] ?? '';
if (!$startDate || !$endDate) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych parametrów']);
    exit;
}

$format = 'Y-m-d';
$startObj = DateTime::createFromFormat($format, $startDate);
$endObj = DateTime::createFromFormat($format, $endDate);
if (!$startObj || $startObj->format($format) !== $startDate ||
    !$endObj || $endObj->format($format) !== $endDate ||
    $startObj > $endObj) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowe daty']);
    exit;
}

try {
    $stmt = $pdo->prepare(
        "SELECT ws.vehicle_plate AS rejestracja, COALESCE(SUM(k.amount), 0) AS turnover
" .
        "  FROM work_sessions ws
" .
        "  JOIN kursy k ON k.driver_id = ws.driver_id
" .
        "             AND DATE(k.date) = DATE(ws.start_time)
" .
        " WHERE DATE(ws.start_time) BETWEEN ? AND ?
" .
        "   AND ws.vehicle_plate IS NOT NULL
" .
        "   AND ws.vehicle_plate <> ''
" .
        " GROUP BY ws.vehicle_plate"
    );
    $stmt->execute([$startDate, $endDate]);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(['status' => 'success', 'data' => $rows]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
