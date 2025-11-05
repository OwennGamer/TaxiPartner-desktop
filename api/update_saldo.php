<?php
$requiredRole = ['administrator', 'admin'];
require_once 'config.php';
require_once 'auth.php';
require_once 'db.php';
require_once __DIR__ . '/fcm_v1.php';
require_once __DIR__ . '/voucher_utils.php';

header('Content-Type: application/json');

$fcmStatus = 'skipped';

// --- Autoryzacja ---
$decodedToken = getAuthenticatedJwt();
if (!$decodedToken) {
    http_response_code(401);
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena", "fcm_status" => $fcmStatus]);
    exit;
}

// --- Dane wejściowe ---
$data = json_decode(file_get_contents("php://input"), true);
$id                 = trim($data['id'] ?? '');
$saldoDelta         = isset($data['saldo_amount']) ? floatval($data['saldo_amount']) : floatval($data['amount'] ?? 0);
$voucherCurrentDelta = floatval($data['voucher_current_amount'] ?? 0);
$voucherPreviousDelta = floatval($data['voucher_previous_amount'] ?? 0);
$reason            = trim($data['reason'] ?? '');
$customReason      = trim($data['custom_reason'] ?? '');

if ($customReason !== '') {
    $reason = $reason === '' ? $customReason : $reason . ': ' . $customReason;
}

if ($id === '' || $reason === '') {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Brak wymaganych danych", "fcm_status" => $fcmStatus]);
    exit;
}

if (abs($saldoDelta) < 1e-6 && abs($voucherCurrentDelta) < 1e-6 && abs($voucherPreviousDelta) < 1e-6) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Brak zmian do zapisania", "fcm_status" => $fcmStatus]);
    exit;
}

// --- Pobierz aktualne saldo ---
$stmt = $pdo->prepare("SELECT id, saldo, voucher_current_amount, voucher_current_month, voucher_previous_amount, voucher_previous_month FROM kierowcy WHERE id = ?");
$stmt->execute([$id]);
$current = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$current) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Kierowca nie znaleziony", "fcm_status" => $fcmStatus]);
    exit;
}

$current = voucher_refresh_buckets($pdo, $current);

$currentSaldo = floatval($current['saldo']);
$newSaldo = $currentSaldo;

$changedSaldo = abs($saldoDelta) >= 1e-6;
$changedVoucherCurrent = abs($voucherCurrentDelta) >= 1e-6;
$changedVoucherPrevious = abs($voucherPreviousDelta) >= 1e-6;

if ($changedSaldo) {
    $newSaldo = $currentSaldo + $saldoDelta;
    $pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?")->execute([$newSaldo, $id]);
    $pdo->prepare("INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod, counter_type) VALUES (?, ?, ?, ?, ?)")
        ->execute([$id, $saldoDelta, $newSaldo, $reason, 'saldo']);
}

if ($changedVoucherCurrent) {
    $before = isset($current['voucher_current_amount']) ? (float)$current['voucher_current_amount'] : 0.0;
    $current = voucher_increment_bucket($pdo, $id, $current, $voucherCurrentDelta, 'current');
    $after = isset($current['voucher_current_amount']) ? (float)$current['voucher_current_amount'] : $before;
    $pdo->prepare("INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod, counter_type) VALUES (?, ?, ?, ?, ?)")
        ->execute([$id, $voucherCurrentDelta, $after, $reason, 'voucher_current']);
}

if ($changedVoucherPrevious) {
    $beforePrev = isset($current['voucher_previous_amount']) ? (float)$current['voucher_previous_amount'] : 0.0;
    $current = voucher_increment_bucket($pdo, $id, $current, $voucherPreviousDelta, 'previous');
    $afterPrev = isset($current['voucher_previous_amount']) ? (float)$current['voucher_previous_amount'] : $beforePrev;
    $pdo->prepare("INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod, counter_type) VALUES (?, ?, ?, ?, ?)")
        ->execute([$id, $voucherPreviousDelta, $afterPrev, $reason, 'voucher_previous']);
}

$voucherCurrentAfter = isset($current['voucher_current_amount']) ? (float)$current['voucher_current_amount'] : 0.0;
$voucherPreviousAfter = isset($current['voucher_previous_amount']) ? (float)$current['voucher_previous_amount'] : 0.0;

// --- Wysyłka powiadomienia FCM (HTTP v1) ---
try {
    // pobierz token urządzenia kierowcy
    $tokenStmt = $pdo->prepare("SELECT fcm_token FROM kierowcy WHERE id = ?");
    $tokenStmt->execute([$id]);
    $driver   = $tokenStmt->fetch(PDO::FETCH_ASSOC);
    $fcmToken = $driver['fcm_token'] ?? null;

    $hasChangesForNotification = $changedSaldo || $changedVoucherCurrent || $changedVoucherPrevious;

    if ($fcmToken && $hasChangesForNotification) {
        $title = 'Zmiana salda';

        $bodyChanges = [];
        $formatSigned = static function (float $value): string {
            $sign = $value >= 0 ? '+' : '';
            return $sign . number_format($value, 2, ',', ' ') . ' zł';
        };

        if ($changedSaldo) {
            $bodyChanges[] = 'saldo ' . $formatSigned($saldoDelta) . ' (po: ' . number_format($newSaldo, 2, ',', ' ') . ' zł)';
        }
        if ($changedVoucherCurrent) {
            $bodyChanges[] = 'vouchery (bieżące) ' . $formatSigned($voucherCurrentDelta) . ' (po: ' . number_format($voucherCurrentAfter, 2, ',', ' ') . ' zł)';
        }
        if ($changedVoucherPrevious) {
            $bodyChanges[] = 'vouchery (poprzednie) ' . $formatSigned($voucherPreviousDelta) . ' (po: ' . number_format($voucherPreviousAfter, 2, ',', ' ') . ' zł)';
        }

        $body = 'Administrator dokonał zmiany: ' . implode(', ', $bodyChanges) . '. Powód: ' . $reason;

        // Dodatkowe dane dla aplikacji (odebranie w onMessageReceived)
        $dataPayload = [
            'type'                     => 'saldo_update',
            'driver_id'                => (string)$id,
            'saldo_change'             => sprintf('%.2f', $saldoDelta),
            'saldo_po'                 => sprintf('%.2f', $newSaldo),
            'voucher_current_change'   => sprintf('%.2f', $voucherCurrentDelta),
            'voucher_current_after'    => sprintf('%.2f', $voucherCurrentAfter),
            'voucher_previous_change'  => sprintf('%.2f', $voucherPreviousDelta),
            'voucher_previous_after'   => sprintf('%.2f', $voucherPreviousAfter),
            'reason'                   => $reason,
            'initiator_role'           => (string)($decodedToken->role ?? ''),
            'initiator_id'             => (string)($decodedToken->user_id ?? ''),
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
    } elseif (!$fcmToken) {
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
        "status"                   => "success",
        "message"                  => "Saldo zaktualizowane",
        "driver_id"                => (string)$id,
        "new_saldo"                => $newSaldo,
        "saldo_change"             => $saldoDelta,
        "voucher_current_amount"   => isset($current['voucher_current_amount']) ? (float)$current['voucher_current_amount'] : 0.0,
        "voucher_current_change"   => $voucherCurrentDelta,
        "voucher_current_after"    => $voucherCurrentAfter,
        "voucher_previous_amount"  => isset($current['voucher_previous_amount']) ? (float)$current['voucher_previous_amount'] : 0.0,
        "voucher_previous_change"  => $voucherPreviousDelta,
        "voucher_previous_after"   => $voucherPreviousAfter,
        "reason"                   => $reason,
        "requested_by"             => [
            "user_id" => $decodedToken->user_id ?? null,
            "role"    => $decodedToken->role ?? null,
        ],
        "fcm_status"               => $fcmStatus
    ], JSON_UNESCAPED_UNICODE);
