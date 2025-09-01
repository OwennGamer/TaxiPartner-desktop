<?php
require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/db.php';
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
    global $secret_key, $pdo;
    try {
        $decoded = JWT::decode($jwt, new Key($secret_key, 'HS256'));

        // sprawdź czy token istnieje w tabeli i nie wygasł
        $stmt = $pdo->prepare("SELECT expires_at FROM jwt_tokens WHERE token = ? LIMIT 1");
        $stmt->execute([$jwt]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$row) {
            return false;
        }

        if (strtotime($row['expires_at']) < time()) {
            return false;
        }

        return $decoded;
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


