<?php
require_once 'config.php';
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
        VALUES (?, ?, ?, ?)"
)->execute([$id, $amount, $newSaldo, $reason]);

// powiadom kierowcę o zmianie przez Firebase Cloud Messaging
if (!function_exists('curl_init')) {
    error_log('curl_init() nie jest dostępna, pomijam wysyłkę FCM');
} else {
    try {
        $tokenStmt = $pdo->prepare("SELECT fcm_token FROM kierowcy gdzie id = ?");
        $tokenStmt->execute([$id]);
        $driver = $tokenStmt->fetch(PDO::FETCH_ASSOC);
        $fcmToken = $driver['fcm_token'] ?? null;

        if ($fcmToken) {
            if (!defined('FCM_SERVER_KEY')) {
                error_log('FCM_SERVER_KEY nie jest zdefiniowany w konfiguracji');
            } else {
                $fcmUrl = 'https://fcm.googleapis.com/fcm/send';
                $headers = [
                    'Authorization: key=' . FCM_SERVER_KEY,
                    'Content-Type: application/json'
                ];
                $payload = [
                    'to' => $fcmToken,
                    'notification' => [
                        'title' => 'Zmiana licznika',
                        'body'  => 'Administrator dokonał zmiany licznika z powodu: ' . $reason
                    ]
                ];

                $ch = curl_init();
                curl_setopt($ch, CURLOPT_URL, $fcmUrl);
                curl_setopt($ch, CURLOPT_POST, true);
                curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
                curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
                curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));

                $result = curl_exec($ch);
                if ($result === false) {
                    error_log('FCM send error: ' . curl_error($ch));
                } else {
                    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
                    if ($httpCode >= 400) {
                        error_log('FCM send failed with HTTP code ' . $httpCode . ': ' . $result);
                    }
                }

                curl_close($ch);
            }
        } else {
            error_log('Brak fcm_token dla kierowcy o ID ' . $id);
        }
    } catch (Throwable $e) {
        error_log('Wyjątek podczas wysyłania FCM: ' . $e->getMessage());
    }
}

echo json_encode([
    "status" => "success",
    "message" => "Saldo zaktualizowane",
    "new_saldo" => $newSaldo
]);
