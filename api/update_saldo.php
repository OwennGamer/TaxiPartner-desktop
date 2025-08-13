<?php
require_once 'config.php';
require_once 'auth.php';
require_once 'db.php';
require_once 'fcm.php';

header('Content-Type: application/json');

$fcmStatus = 'skipped';

$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena", "fcm_status" => $fcmStatus]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);
$id = trim($data['id'] ?? '');
$amount = floatval($data['amount'] ?? 0);
$reason = trim($data['reason'] ?? '');
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

// pobierz aktualne saldo
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

// zaktualizuj saldo
$pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?")->execute([$newSaldo, $id]);

// zapisz historię
$pdo->prepare("
    INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod)
        VALUES (?, ?, ?, ?)"
)->execute([$id, $amount, $newSaldo, $reason]);

// powiadom kierowcę o zmianie przez Firebase Cloud Messaging
if (!function_exists('curl_init')) {
    error_log('curl_init() nie jest dostępna, pomijam wysyłkę FCM');
    $fcmStatus = 'skipped';
} else {
    try {
        $tokenStmt = $pdo->prepare("SELECT fcm_token FROM kierowcy gdzie id = ?");
        $tokenStmt->execute([$id]);
        $driver = $tokenStmt->fetch(PDO::FETCH_ASSOC);
        $fcmToken = $driver['fcm_token'] ?? null;

        if ($fcmToken) {
            $message = [
                'token' => $fcmToken,
                'notification' => [
                    'title' => 'Zmiana licznika',
                    'body'  => 'Administrator dokonał zmiany licznika z powodu: ' . $reason
                ]
            ];

            try {
                $response = sendFcmMessage(FIREBASE_PROJECT_ID, $message);
                $statusCode = $response['statusCode'] ?? 0;

                if ($statusCode >= 200 && $statusCode < 300) {
                    $fcmStatus = 'sent';
                } else {
                    error_log('FCM send failed with HTTP code ' . $statusCode . ': ' . ($response['body'] ?? ''));
                    $fcmStatus = 'error';
                }
            } catch (Throwable $e) {
                error_log('Wyjątek podczas wysyłania FCM: ' . $e->getMessage());
                $fcmStatus = 'error';
            }
        } else {
            error_log('Brak fcm_token dla kierowcy o ID ' . $id);
            $fcmStatus = 'skipped';
        }
    } catch (Throwable $e) {
        error_log('Wyjątek podczas przygotowania FCM: ' . $e->getMessage());
        $fcmStatus = 'error';
    }
}

if ($fcmStatus === 'error') {
    http_response_code(207);
} else {
    http_response_code(200);
}

echo json_encode([
    "status" => "success",
    "message" => "Saldo zaktualizowane",
    "new_saldo" => $newSaldo,
    "fcm_status" => $fcmStatus
]);
