<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);
$driverId = $data['id'];

try {
    // Usuń warunki współpracy
    $stmt = $pdo->prepare("DELETE FROM collaboration_terms WHERE driver_id = ?");
    $stmt->execute([$driverId]);

    // Usuń kierowcę
    $stmt = $pdo->prepare("DELETE FROM kierowcy WHERE id = ?");
    $stmt->execute([$driverId]);

    echo json_encode(["status" => "success", "message" => "Kierowca usunięty."]);

} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd SQL: " . $e->getMessage()]);
}
