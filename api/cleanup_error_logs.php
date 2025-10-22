<?php
require_once __DIR__ . '/db.php';

// Ten skrypt można uruchamiać z CRON-a raz dziennie.
// Usuwa logi starsze niż 60 dni, aby nie zapełniać serwera.

try {
    $stmt = $pdo->prepare('DELETE FROM app_error_logs WHERE created_at < DATE_SUB(NOW(), INTERVAL 60 DAY)');
    $stmt->execute();

    $removed = $stmt->rowCount();
    echo sprintf("Removed %d old error logs\n", $removed);
} catch (Throwable $e) {
    error_log('cleanup_error_logs.php failed: ' . $e->getMessage());
    http_response_code(500);
    echo "cleanup_error_logs.php failed\n";
    exit(1);
}
