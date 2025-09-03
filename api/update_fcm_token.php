<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    http_response_code(401);
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

if ($decoded->role !== 'driver' && $decoded->role !== 'flotowiec') {
    http_response_code(403);
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true) ?? [];
$fcmToken = trim($input['fcm_token'] ?? ($_POST['fcm_token'] ?? ''));

if ($fcmToken === '') {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Brak tokena FCM"]);
    exit;
}

$driverId = $decoded->user_id;

try {
    $stmt = $pdo->prepare('UPDATE kierowcy SET fcm_token = ? WHERE id = ?');
    $stmt->execute([$fcmToken, $driverId]);

    echo json_encode(["status" => "success", "message" => "Token zaktualizowany"]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
