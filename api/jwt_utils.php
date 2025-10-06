<?php
require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/db.php';
use Firebase\JWT\JWT;
use Firebase\JWT\Key;

// Czas ważności tokena ustawiamy na 10 lat – w praktyce oznacza to brak
// samoczynnego wylogowania użytkownika.
if (!defined('TOKEN_TTL_SECONDS')) {
    define('TOKEN_TTL_SECONDS', 10 * 365 * 24 * 60 * 60);
}

// Pozwalamy bibliotece zaakceptować tokeny nawet, jeśli w polu exp znajduje
// się krótsza ważność (np. wygenerowane przed aktualizacją). Dzięki temu
// użytkownik nie zostanie wylogowany.
if (!property_exists(JWT::class, 'leeway') || JWT::$leeway < TOKEN_TTL_SECONDS) {
    JWT::$leeway = TOKEN_TTL_SECONDS;
}

$secret_key = "tajny_klucz"; // 🔴 Zmień na bardziej skomplikowany klucz!

function getTokenExpiryDate(): string
{
    return date('Y-m-d H:i:s', time() + TOKEN_TTL_SECONDS);
}

// Funkcja generująca token JWT
function generateJWT($user_id, $role, $device_id) {
    global $secret_key;

    $payload = [
        "iat" => time(),
        "exp" => time() + TOKEN_TTL_SECONDS,
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

        // Sprawdź, czy token istnieje w tabeli i ewentualnie odśwież jego datę
        // ważności – dzięki temu jedynym powodem unieważnienia pozostaje zdalne
        // wylogowanie.
        $stmt = $pdo->prepare("SELECT expires_at FROM jwt_tokens WHERE token = ? LIMIT 1");
        $stmt->execute([$jwt]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$row) {
            return false;
        }

        if (!empty($row['expires_at']) && strtotime($row['expires_at']) < time()) {
            try {
                $newExpiry = getTokenExpiryDate();
                $update = $pdo->prepare('UPDATE jwt_tokens SET expires_at = ? WHERE token = ?');
                $update->execute([$newExpiry, $jwt]);
            } catch (Exception $e) {
                // Jeśli aktualizacja się nie uda, nadal traktujemy token jako ważny.
            }
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


