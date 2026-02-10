<?php
// reset_driver_session_cli.php
// Narzędzie awaryjne do odblokowania konta kierowcy (CLI only).

if (PHP_SAPI !== 'cli') {
    http_response_code(403);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'status' => 'error',
        'message' => 'To narzędzie można uruchomić wyłącznie w trybie CLI.'
    ], JSON_UNESCAPED_UNICODE);
    exit(1);
}

require_once __DIR__ . '/db.php';

$driverId = $argv[1] ?? '';
$deviceId = $argv[2] ?? null;

if ($driverId === '') {
    fwrite(STDERR, "Użycie: php api/reset_driver_session_cli.php <DRIVER_ID> [DEVICE_ID]\n");
    exit(1);
}

function tableExists(PDO $pdo, string $table): bool
{
    $stmt = $pdo->prepare('SHOW TABLES LIKE ?');
    $stmt->execute([$table]);
    return $stmt->fetchColumn() !== false;
}

try {
    $pdo->beginTransaction();

    $summary = [
        'driver_id' => $driverId,
        'device_id' => $deviceId,
        'deleted' => [
            'jwt_tokens' => 0,
            'driver_sessions' => 0,
            'sessions' => 0,
        ],
        'updated' => [
            'fcm_token_cleared' => 0,
        ],
    ];

    $clearFcm = $pdo->prepare('UPDATE kierowcy SET fcm_token = NULL WHERE id = ?');
    $clearFcm->execute([$driverId]);
    $summary['updated']['fcm_token_cleared'] = $clearFcm->rowCount();

    if (tableExists($pdo, 'jwt_tokens')) {
        if ($deviceId !== null && $deviceId !== '') {
            $delJwt = $pdo->prepare('DELETE FROM jwt_tokens WHERE driver_id = ? AND device_id = ?');
            $delJwt->execute([$driverId, $deviceId]);
        } else {
            $delJwt = $pdo->prepare('DELETE FROM jwt_tokens WHERE driver_id = ?');
            $delJwt->execute([$driverId]);
        }
        $summary['deleted']['jwt_tokens'] = $delJwt->rowCount();
    }

    foreach (['driver_sessions', 'sessions'] as $table) {
        if (!tableExists($pdo, $table)) {
            continue;
        }

        $del = $pdo->prepare("DELETE FROM {$table} WHERE driver_id = ?");
        $del->execute([$driverId]);
        $summary['deleted'][$table] = $del->rowCount();
    }

    $pdo->commit();

    echo json_encode([
        'status' => 'success',
        'message' => 'Sesja kierowcy została zresetowana. Użytkownik może zalogować się ponownie.',
        'summary' => $summary,
    ], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT) . PHP_EOL;
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }

    fwrite(STDERR, "Błąd resetowania sesji: {$e->getMessage()}\n");
    exit(1);
}
