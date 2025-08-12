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

if ($decoded->role !== 'admin' && $decoded->role !== 'flotowiec' && $decoded->role !== 'driver') {
    http_response_code(403);
    echo json_encode(['status' => 'error', 'message' => 'Brak uprawnień']);
    exit;
}

$driverId = $_GET['driver_id'] ?? '';
if ($driverId === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak ID kierowcy']);
    exit;
}

try {
    $stmt = $pdo->prepare('SELECT fcm_token FROM kierowcy WHERE id = ?');
    $stmt->execute([$driverId]);
    $fcmToken = $stmt->fetchColumn();

    if ($fcmToken === false || $fcmToken === '') {
        echo json_encode(['status' => 'error', 'message' => 'Nie znaleziono tokena']);
    } else {
        echo json_encode(['status' => 'success', 'fcm_token' => $fcmToken]);
    }
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
