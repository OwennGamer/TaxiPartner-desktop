<?php
// healthcheck.php – endpoint sprawdzający połączenie z bazą danych

ini_set('display_errors', 0);
error_reporting(0);

header('Content-Type: application/json; charset=utf-8');

require_once 'db.php';

try {
    $pdo->query('SELECT 1');
    echo json_encode(["status" => "ok"]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
