<?php
// Script to remove outdated receipt photos and clear DB reference
// Deletes files from api/uploads/receipts/ when the associated kursy.date is older than 14 days.

require_once __DIR__ . '/db.php';

try {
    // Select entries older than 14 days that have a receipt photo
    $stmt = $pdo->prepare(
        'SELECT id, receipt_photo FROM kursy
         WHERE receipt_photo IS NOT NULL AND date < DATE_SUB(NOW(), INTERVAL 14 DAY)'
    );
    $stmt->execute();
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($rows as $row) {
        $path = $row['receipt_photo'];
        if ($path && strpos($path, 'uploads/receipts/') === 0) {
            $fullPath = __DIR__ . '/' . $path;
            if (file_exists($fullPath)) {
                @unlink($fullPath);
            }
        }
        // Clear receipt_photo reference in the database
        $update = $pdo->prepare('UPDATE kursy SET receipt_photo = NULL WHERE id = ?');
        $update->execute([$row['id']]);
    }
} catch (Exception $e) {
    // Log any errors to standard error for cron logs
    error_log('Receipt cleanup failed: ' . $e->getMessage());
    exit(1);
}
