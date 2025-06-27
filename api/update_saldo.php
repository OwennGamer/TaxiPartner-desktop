<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);
$id = trim($data['id'] ?? '');
$amount = floatval($data['amount'] ?? 0);
$reason = trim($data['reason'] ?? '');
$customReason = trim($data['custom_reason'] ?? '');

if ($id === '' || $reason === '') {
    echo json_encode(["status" => "error", "message" => "Brak wymaganych danych"]);
    exit;
}

// Jeśli powód to "inny", dołącz opis
if ($reason === 'inny' && $customReason !== '') {
    $reason .= ': ' . $customReason;
}

// pobierz aktualne saldo
$stmt = $pdo->prepare("SELECT saldo FROM kierowcy WHERE id = ?");
$stmt->execute([$id]);
$current = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$current) {
    echo json_encode(["status" => "error", "message" => "Kierowca nie znaleziony"]);
    exit;
}

$currentSaldo = floatval($current['saldo']);
$newSaldo = $currentSaldo + $amount;

// zaktualizuj saldo
$pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?")->execute([$newSaldo, $id]);

// zapisz historię
$pdo->prepare("
    INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod)
    VALUES (?, ?, ?, ?)
")->execute([$id, $amount, $newSaldo, $reason]);

echo json_encode([
    "status" => "success",
    "message" => "Saldo zaktualizowane",
    "new_saldo" => $newSaldo
]);
