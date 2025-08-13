<?php
header("Content-Type: application/json");

require_once 'db.php';
require_once 'jwt_utils.php'; // Upewnij się, że ten plik obsługuje createJWT()

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['username']) || !isset($data['password'])) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Brak wymaganych danych"]);
    exit;
}

$username = $data['username'];
$password = $data['password'];

try {
    $stmt = $pdo->prepare("SELECT id, username, password, role FROM users WHERE username = ?");
    $stmt->execute([$username]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        http_response_code(401);
        echo json_encode(["status" => "error", "message" => "Nie znaleziono użytkownika"]);
        exit;
    }
    
        // Weryfikacja hasła i ewentualna migracja ze starych algorytmów
    $stored = $user['password'];
    $ok = false;

    // 1) Nowoczesny hasz (bcrypt itp.)
    if (password_get_info($stored)['algo'] !== 0 && password_verify($password, $stored)) {
        $ok = true;
        // Zaktualizuj hasz jeśli stosowany algorytm jest przestarzały
        if (password_needs_rehash($stored, PASSWORD_DEFAULT)) {
            $newHash = password_hash($password, PASSWORD_DEFAULT);
            $update = $pdo->prepare("UPDATE users SET password = ? WHERE id = ?");
            $update->execute([$newHash, $user['id']]);
        }
    }
    // 2) Stary hasz SHA-256
    elseif (preg_match('/^[0-9a-f]{64}$/i', $stored) && hash_equals($stored, hash('sha256', $password))) {
        $ok = true;
        $newHash = password_hash($password, PASSWORD_DEFAULT);
        $update = $pdo->prepare("UPDATE users SET password = ? WHERE id = ?");
        $update->execute([$newHash, $user['id']]);
    }
    // 3) Hasło w postaci czystego tekstu
    elseif ($password === $stored) {
        $ok = true;
        $newHash = password_hash($password, PASSWORD_DEFAULT);
        $update = $pdo->prepare("UPDATE users SET password = ? WHERE id = ?");
        $update->execute([$newHash, $user['id']]);
    }

    if (!$ok) {

        http_response_code(401);
        echo json_encode(["status" => "error", "message" => "Niepoprawne hasło"]);
        exit;
    }

    // Tworzymy token JWT
    $payload = [
        "user_id" => $user['id'],
        "username" => $user['username'],
        "role" => $user['role']
    ];

    $jwt = generateJWT($user['id'], $user['role']);

    echo json_encode([
        "status" => "success",
        "message" => "Zalogowano pomyślnie",
        "token" => $jwt
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
?>
