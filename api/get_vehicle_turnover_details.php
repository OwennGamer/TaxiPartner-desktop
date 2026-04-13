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
$plate = trim($_GET['vehicle_plate'] ?? ($_GET['rejestracja'] ?? ''));

if (!$startDate || !$endDate || $plate === '') {
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
        "SELECT k.id,
                k.date,
                k.driver_id,
                k.type AS payment_type,
                k.source AS type,
                k.amount,
                ws.start_time AS session_start,
                ws.end_time AS session_end,
                ws.vehicle_plate AS rejestracja
           FROM work_sessions ws
           JOIN kursy k ON k.driver_id = ws.driver_id
                      AND k.date >= ws.start_time
                      AND k.date <= COALESCE(ws.end_time, NOW())
          WHERE DATE(k.date) BETWEEN ? AND ?
            AND UPPER(TRIM(ws.vehicle_plate)) = UPPER(TRIM(?))
          ORDER BY k.date DESC"
    );
    $stmt->execute([$startDate, $endDate, $plate]);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $sum = 0.0;
    foreach ($rows as $row) {
        $sum += (float)($row['amount'] ?? 0);
    }

    echo json_encode([
        'status' => 'success',
        'sum' => $sum,
        'count' => count($rows),
        'data' => $rows,
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
