<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

// Autoryzacja
$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

// Odbieranie danych
$data = json_decode(file_get_contents("php://input"), true);

// Log do testów
file_put_contents("final_log.txt", print_r($data, true));

// Zmienne
$id = $data['id'];
$imie = $data['imie'];
$nazwisko = $data['nazwisko'];
$password = $data['password'];
$status = $data['status'];

$percentTurnover = $data['percentTurnover'];
$fuelCost = $data['fuelCost'];
$cardCommission = $data['cardCommission'];
$partnerCommission = $data['partnerCommission'];
$boltCommission = $data['boltCommission'];
$settlementLimit = $data['settlementLimit'];

try {
    // 🔐 Haszuj hasło tylko jeśli zmienione
    $stmt = $pdo->prepare("SELECT password FROM kierowcy WHERE id = ?");
    $stmt->execute([$id]);
    $existingPassword = $stmt->fetchColumn();

        // Jeżeli hasło zostało zmienione, haszuj je przy użyciu password_hash
    if (!password_verify($password, $existingPassword)) {
        $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
    } else {
        $hashedPassword = $existingPassword;
    }

    // 🧾 Aktualizacja tabeli kierowcy
    $stmt = $pdo->prepare("UPDATE kierowcy SET imie = ?, nazwisko = ?, password = ?, status = ? WHERE id = ?");
    $stmt->execute([$imie, $nazwisko, $hashedPassword, $status, $id]);

    // 🧾 Aktualizacja collaboration_terms
    $terms = [
        'percentTurnover' => $percentTurnover,
        'fuelCost' => $fuelCost,
        'cardCommission' => $cardCommission,
        'partnerCommission' => $partnerCommission,
        'boltCommission' => $boltCommission,
        'settlementLimit' => $settlementLimit
    ];

    foreach ($terms as $key => $value) {
        $checkStmt = $pdo->prepare("SELECT COUNT(*) FROM collaboration_terms WHERE driver_id = ? AND term_name = ?");
        $checkStmt->execute([$id, $key]);
        $exists = $checkStmt->fetchColumn();

        if ($exists) {
            $updateStmt = $pdo->prepare("UPDATE collaboration_terms SET term_value = ? WHERE driver_id = ? AND term_name = ?");
            $updateStmt->execute([$value, $id, $key]);
        } else {
            $insertStmt = $pdo->prepare("INSERT INTO collaboration_terms (driver_id, term_name, term_value) VALUES (?, ?, ?)");
            $insertStmt->execute([$id, $key, $value]);
        }
    }

    echo json_encode(["status" => "success", "message" => "Zaktualizowano dane kierowcy."]);

} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd SQL: " . $e->getMessage()]);
}
