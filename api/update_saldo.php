<?php
require_once 'config.php';
require_once 'auth.php';
require_once 'db.php';
require_once __DIR__ . '/fcm_v1.php';

header('Content-Type: application/json');

$fcmStatus = 'skipped';

// --- Autoryzacja ---
$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena", "fcm_status" => $fcmStatus]);
    exit;
}

// --- Dane wejściowe ---
$data = json_decode(file_get_contents("php://input"), true);
$id           = trim($data['id'] ?? '');
$amount       = floatval($data['amount'] ?? 0);
$reason       = trim($data['reason'] ?? '');
$customReason = trim($data['custom_reason'] ?? '');

if ($id === '' || $reason === '') {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Brak wymaganych danych", "fcm_status" => $fcmStatus]);
    exit;
}

// Jeśli powód to "inny", dołącz opis
if ($reason === 'inny' && $customReason !== '') {
    $reason .= ': ' . $customReason;
}

// --- Pobierz aktualne saldo ---
$stmt = $pdo->prepare("SELECT saldo FROM kierowcy WHERE id = ?");
$stmt->execute([$id]);
$current = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$current) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Kierowca nie znaleziony", "fcm_status" => $fcmStatus]);
    exit;
}

$currentSaldo = floatval($current['saldo']);
$newSaldo = $currentSaldo + $amount;

// --- Aktualizacja salda ---
$pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?")->execute([$newSaldo, $id]);

// --- Historia zmian ---
$pdo->prepare("
    INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod)
    VALUES (?, ?, ?, ?)
")->execute([$id, $amount, $newSaldo, $reason]);

// --- Wysyłka powiadomienia FCM (HTTP v1) ---
try {
    // pobierz token urządzenia kierowcy
    $tokenStmt = $pdo->prepare("SELECT fcm_token FROM kierowcy WHERE id = ?");
    $tokenStmt->execute([$id]);
    $driver   = $tokenStmt->fetch(PDO::FETCH_ASSOC);
    $fcmToken = $driver['fcm_token'] ?? null;

    if ($fcmToken) {
        $title = 'Zmiana salda';
        $body  = 'Administrator dokonał zmiany salda z powodu: ' . $reason;

        // Dodatkowe dane dla aplikacji (odebranie w onMessageReceived)
        $dataPayload = [
            'type'      => 'saldo_update',
            'driver_id' => (string)$id,
            'amount'    => (string)$amount,
            'saldo_po'  => (string)$newSaldo,
        ];

        // Wyślij FCM przez HTTP v1
        $resp = sendFcmV1($fcmToken, $title, $body, $dataPayload);
        if ($resp === null) {
            $fcmStatus = 'skipped:no_credentials';
        } else {
            $fcmStatus = 'sent';
            // Dodatkowy log obok logu z fcm_v1.php
            @file_put_contents(__DIR__ . '/debug_fcm.log', date('c') . ' ' . json_encode([
                'status' => $fcmStatus,
                'resp'   => $resp
            ], JSON_UNESCAPED_UNICODE) . PHP_EOL, FILE_APPEND);
            @chmod(__DIR__ . '/debug_fcm.log', 0666);
        }
    } else {
        error_log('Brak fcm_token dla kierowcy o ID ' . $id);
        $fcmStatus = 'skipped:no_token';
    }
} catch (Throwable $e) {
    error_log('Wyjątek podczas wysyłania FCM: ' . $e->getMessage());
    $fcmStatus = 'error:' . $e->getMessage();
}

// --- Kody HTTP zależnie od wysyłki FCM ---
if (str_starts_with($fcmStatus, 'error')) {
    http_response_code(207); // saldo ok, ale FCM nie poszło
} else {
    http_response_code(200); // pełny sukces lub pominięcie wysyłki
}

// --- Odpowiedź ---
echo json_encode([
    "status"     => "success",
    "message"    => "Saldo zaktualizowane",
    "new_saldo"  => $newSaldo,
    "fcm_status" => $fcmStatus
], JSON_UNESCAPED_UNICODE);
