<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

if ($decoded->role !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

$data = json_decode(file_get_contents('php://input'), true);
$empId = $data['id'] ?? null;
if (!$empId) {
    echo json_encode(["status" => "error", "message" => "Brak id"]);
    exit;
}

try {
    $stmt = $pdo->prepare("DELETE FROM pracownicy WHERE id = ?");
    $stmt->execute([$empId]);
    echo json_encode(["status" => "success"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd SQL: " . $e->getMessage()]);
}
?>
