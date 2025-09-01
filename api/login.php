<?php
// login.php – endpoint logowania kierowcy

ini_set('display_errors', 0);
error_reporting(0);

header('Content-Type: application/json; charset=utf-8');

require_once 'db.php';
require_once 'jwt_utils.php';

if (empty($_POST['driver_id']) || empty($_POST['password']) || empty($_POST['device_id'])) {
    http_response_code(400);
    echo json_encode([
        "status"  => "error",
        "message" => "Brak wymaganych danych"
    ]);
    exit;
}

$driver_id = $_POST['driver_id'];
$password  = $_POST['password'];
$device_id = $_POST['device_id'];

try {
        // 1) Pobierz rekord kierowcy wraz z przypisaną rolą
    $stmt = $pdo->prepare("SELECT id, password, rola FROM kierowcy WHERE id = ?");
    $stmt->execute([$driver_id]);
    $user = $stmt->fetch();

    if (!$user) {
        http_response_code(404);
        echo json_encode([
            "status"  => "error",
            "message" => "Nie znaleziono kierowcy"
        ]);
        exit;
    }

    $stored = $user['password'];
    $ok     = false;

    // 2) Sprawdź bcrypt (nowe hasze zaczynają się od $2y$ lub $2a$)
    if ((strpos($stored, '$2y$') === 0 || strpos($stored, '$2a$') === 0)
        && password_verify($password, $stored)
    ) {
        $ok = true;
    }
    // 3) Albo SHA-256 hex (stare hasze 64-znakowe)
    elseif (preg_match('/^[0-9a-f]{64}$/i', $stored)
        && hash('sha256', $password) === $stored
    ) {
        $ok = true;
        // ▷ migracja: zamień na bcrypt
        $newHash = password_hash($password, PASSWORD_DEFAULT);
        $migrate = $pdo->prepare("UPDATE kierowcy SET password = ? WHERE id = ?");
        $migrate->execute([$newHash, $driver_id]);
    }
    // 4) Albo plain-text (jeszcze starsze)
    elseif ($password === $stored) {
        $ok = true;
    }

    if (!$ok) {
        http_response_code(401);
        echo json_encode([
            "status"  => "error",
            "message" => "Niepoprawne hasło"
        ]);
        exit;
    }

    // 5) Generujemy JWT, zapisujemy go w bazie i zwracamy dane kierowcy wraz z rolą
    $token = generateJWT($user['id'], $user['rola'], $device_id);
    // zapisz token w tabeli jwt_tokens
    try {
        $expiresAt = date('Y-m-d H:i:s', time() + 28800); // 8 godzin
        $ins = $pdo->prepare("INSERT INTO jwt_tokens (token, driver_id, device_id, expires_at) VALUES (?, ?, ?, ?)");
        $ins->execute([$token, $user['id'], $device_id, $expiresAt]);
    } catch (Exception $e) {
        // jeśli zapis nie powiedzie się, kontynuujemy bez przerwania logowania
    }

    echo json_encode([
        "status"    => "success",
        "message"   => "Zalogowano pomyślnie",
        "token"     => $token,
        "driver_id" => $user['id'],
        "rola"      => $user['rola']
    ]);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode([
        "status"  => "error",
        "message" => "Błąd serwera"
    ]);
}
