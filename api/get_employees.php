<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}
$decoded = verifyJWT($token);
if ($decoded->role !== 'admin' && $decoded->role !== 'flotowiec') {
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

try {
    $stmt = $pdo->query("SELECT * FROM pracownicy");
    $employees = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode(["status" => "success", "data" => $employees]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd pobierania danych"]);
}


