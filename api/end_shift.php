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
$end_odometer = isset($_POST['end_odometer']) ? intval($_POST['end_odometer']) : null;

if ($end_odometer === null) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak przebiegu końcowego']);
    exit;
}

try {
    $stmt = $pdo->prepare(
        "SELECT id FROM work_sessions WHERE driver_id = ? AND end_time IS NULL ORDER BY start_time DESC LIMIT 1"
    );
    $stmt->execute([$driver_id]);
    $session = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$session) {
        http_response_code(404);
        echo json_encode(['status' => 'error', 'message' => 'Brak otwartej sesji']);
        exit;
    }

    $session_id = $session['id'];
    $update = $pdo->prepare(
        "UPDATE work_sessions SET end_time = NOW(), end_odometer = ? WHERE id = ?"
    );
    $update->execute([$end_odometer, $session_id]);

    echo json_encode(['status' => 'success', 'session_id' => (int)$session_id]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
