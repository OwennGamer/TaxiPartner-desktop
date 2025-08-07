<?php
header("Content-Type: application/json");

require_once 'db.php';
require_once 'jwt_utils.php'; // Upewnij się, że ten plik obsługuje createJWT()

$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['username']) || !isset($data['password'])) {
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
        echo json_encode(["status" => "error", "message" => "Nie znaleziono użytkownika"]);
        exit;
    }
file_put_contents("debug_log.txt", "Hasło z bazy: {$user['password']}\n", FILE_APPEND);
file_put_contents("debug_log.txt", "Hasło od użytkownika: " . hash('sha256', $password) . "\n", FILE_APPEND);

    // Porównanie hasła zahashowanego
    if (!hash_equals($user['password'], hash('sha256', $password))) {
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
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
?>
