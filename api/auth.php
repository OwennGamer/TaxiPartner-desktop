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

// Odczyt identyfikatora urządzenia z nagłówka
$deviceId = $_SERVER['HTTP_DEVICE_ID'] ?? null;

// Weryfikacja tokena
$decoded = verifyJWT($token);
file_put_contents('debug_auth.log', "DECODED: " . json_encode($decoded) . PHP_EOL, FILE_APPEND);

if (!$decoded || !$deviceId || ($decoded->device_id ?? '') !== $deviceId) {
    echo json_encode(["status" => "error", "message" => "Nieprawidłowy token"]);
    http_response_code(401);
    exit;
}

if (isset($requiredRole)) {
    $tokenRole = strtolower($decoded->role ?? '');
    if ($tokenRole !== strtolower($requiredRole)) {
        echo json_encode(["status" => "error", "message" => "Niewystarczające uprawnienia"]);
        http_response_code(403);
        exit;
    }
}

$GLOBALS['AUTHENTICATED_JWT'] = $decoded;

function getAuthenticatedJwt()
{
    return $GLOBALS['AUTHENTICATED_JWT'] ?? null;
}

?>


