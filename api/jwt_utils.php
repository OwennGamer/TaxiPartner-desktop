<?php
require_once __DIR__ . '/vendor/autoload.php';
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$secret_key = "tajny_klucz"; // 🔴 Zmień na bardziej skomplikowany klucz!

// Funkcja generująca token JWT
function generateJWT($user_id, $role, $device_id) {
    global $secret_key;

    $payload = [
        "iat" => time(),
        "exp" => time() + 28800,
        "user_id" => $user_id,
        "role" => $role,  // 🟢 Dodajemy rolę do tokena
        "device_id" => $device_id
    ];

    return JWT::encode($payload, $secret_key, 'HS256');
}

// Funkcja weryfikująca token JWT
function verifyJWT($jwt) {
    global $secret_key;
    try {
        return JWT::decode($jwt, new Key($secret_key, 'HS256'));
    } catch (Exception $e) {
        return false;
    }
}

// Pobranie tokena z nagłówka
function getAuthorizationHeader() {
    if (!isset($_SERVER['HTTP_AUTHORIZATION'])) {
        return null;
    }
    return trim(str_replace("Bearer ", "", $_SERVER['HTTP_AUTHORIZATION']));
}
?>


