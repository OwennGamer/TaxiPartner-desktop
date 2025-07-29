<?php
require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
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

$driver_id = $decoded->user_id;
$vehicle_plate = trim($_POST['vehicle_plate'] ?? '');
$start_odometer = isset($_POST['start_odometer']) ? intval($_POST['start_odometer']) : null;

if ($vehicle_plate === '' || $start_odometer === null) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

try {
    $stmt = $pdo->prepare(
        "INSERT INTO work_sessions (driver_id, vehicle_plate, start_time, start_odometer) VALUES (?, ?, NOW(), ?)"
    );
    $stmt->execute([$driver_id, $vehicle_plate, $start_odometer]);
    $id = $pdo->lastInsertId();
    echo json_encode(['status' => 'success', 'session_id' => (int)$id]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
