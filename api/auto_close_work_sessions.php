<?php
require_once __DIR__ . '/db.php';

header('Content-Type: application/json; charset=utf-8');

if (PHP_SAPI !== 'cli') {
    http_response_code(403);
    echo json_encode(['status' => 'error', 'message' => 'Forbidden']);
    exit;
}

try {
    $stmt = $pdo->prepare(
        "UPDATE work_sessions
         SET end_time = DATE_ADD(start_time, INTERVAL 12 HOUR),
             end_odometer = NULL
         WHERE end_time IS NULL
           AND start_time <= DATE_SUB(NOW(), INTERVAL 12 HOUR)"
    );
    $stmt->execute();

    echo json_encode([
        'status' => 'success',
        'closed_sessions' => $stmt->rowCount(),
    ], JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    fwrite(STDERR, 'DB error: ' . $e->getMessage() . PHP_EOL);
    exit(1);
}
