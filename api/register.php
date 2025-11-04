<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$input = file_get_contents("php://input");
$data = json_decode($input, true);

if (!$data || !isset($data['username']) || !isset($data['password']) || !isset($data['role'])) {
    echo json_encode(["status" => "error", "message" => "Brak wymaganych danych"]);
    exit;
}

// Obsługiwane role użytkowników
$allowed_roles = ["kierowca", "flotowiec", "administrator", "admin"];
if (!in_array($data['role'], $allowed_roles)) {
    echo json_encode(["status" => "error", "message" => "Nieprawidłowa rola użytkownika"]);
    exit;
}

// Hashowanie hasła
$hashed_password = password_hash($data['password'], PASSWORD_BCRYPT);

try {
    $stmt = $pdo->prepare("INSERT INTO users (username, password, role) VALUES (:username, :password, :role)");
    $stmt->execute([
        'username' => $data['username'],
        'password' => $hashed_password,
        'role' => $data['role']
    ]);

    echo json_encode(["status" => "success", "message" => "Użytkownik zarejestrowany pomyślnie"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd rejestracji: " . $e->getMessage()]);
}
?>
