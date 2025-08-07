<?php
require_once 'jwt_utils.php';

header('Content-Type: application/json');

// Pobranie tokena z nagłówka
$token = getAuthorizationHeader();
file_put_contents('debug_auth.log', "TOKEN: " . $token . PHP_EOL, FILE_APPEND);

if (!$token) {
    echo json_encode(["status" => "error", "message" => "Brak tokena"]);
    http_response_code(401);
    exit;
}

// Weryfikacja tokena
$decoded = verifyJWT($token);
file_put_contents('debug_auth.log', "DECODED: " . json_encode($decoded) . PHP_EOL, FILE_APPEND);

if (!$decoded) {
    echo json_encode(["status" => "error", "message" => "Nieprawidłowy token"]);
    http_response_code(401);
    exit;
}
?>


