<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();

if (!$token || !verifyJWT($token)) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

$decoded = verifyJWT($token);

if ($decoded->role !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);
file_put_contents(__DIR__ . "/final_log.txt", print_r($data, true));

try {
    $pdo->beginTransaction();

    // 1. Dodaj kierowcę
    $stmt = $pdo->prepare("INSERT INTO kierowcy
        (id, imie, nazwisko, password, status, rola, saldo, koszt_paliwa, created_at)
        VALUES (:id, :imie, :nazwisko, :password, :status, :rola, :saldo, :koszt_paliwa, NOW())");

    // haszujemy hasło SHA-256, bo login.php oczekuje takiego formatu
    $hashedPassword = hash('sha256', $data['password']);

    $stmt->execute([
        ':id'          => $data['id'],
        ':imie'        => $data['imie'],
        ':nazwisko'    => $data['nazwisko'],
        ':password'    => $hashedPassword,
        ':status'      => $data['status'],
        ':rola'        => $data['rola'],
        ':saldo'       => $data['saldo'],
        ':koszt_paliwa'=> $data['fuelCost'] == 0 ? 'firma' : 'kierowca'
    ]);

    // 2. Dodaj warunki współpracy do collaboration_terms (1 wpis = 1 warunek)
    $terms = [
        'percentTurnover' => $data['percentTurnover'],
        'fuelCost'        => $data['fuelCost'],
        'cardCommission'  => $data['cardCommission'],
        'partnerCommission'=> $data['partnerCommission'],
        'boltCommission'  => $data['boltCommission'],
        'settlementLimit' => $data['settlementLimit']
    ];

    $stmt2 = $pdo->prepare("INSERT INTO collaboration_terms (driver_id, term_name, term_value) VALUES (:driver_id, :term_name, :term_value)");

    foreach ($terms as $name => $value) {
        file_put_contents(__DIR__ . "/final_log.txt", "\n▶ Wstawiam: $name = $value", FILE_APPEND);
        $stmt2->execute([
            ':driver_id' => $data['id'],
            ':term_name' => $name,
            ':term_value'=> $value
        ]);
        file_put_contents(__DIR__ . "/final_log.txt", "\n✅ Wstawiono: $name", FILE_APPEND);
    }

    $pdo->commit();

    file_put_contents(__DIR__ . "/final_log.txt", "\n✅ Kierowca i warunki współpracy dodane (z osobnymi wpisami)", FILE_APPEND);
    echo json_encode(['status' => 'success', 'message' => 'Kierowca i warunki współpracy dodani.']);

} catch (PDOException $e) {
    $pdo->rollBack();
    file_put_contents(__DIR__ . "/final_log.txt", "\n❌ Błąd SQL: " . $e->getMessage(), FILE_APPEND);
    echo json_encode(['status' => 'error', 'message' => 'Błąd SQL: ' . $e->getMessage()]);
}
?>
