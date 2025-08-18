<?php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/config.php';
require_once __DIR__ . '/fcm_v1.php';

$raw = file_get_contents('php://input');
$in  = json_decode($raw, true);

$token   = $in['token']   ?? null;
$title   = $in['title']   ?? 'Saldo';
$message = $in['message'] ?? 'Test FCM (v1)';
$data    = isset($in['data']) && is_array($in['data']) ? $in['data'] : [];

if (!$token) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak pola token']);
    exit;
}

try {
    $resp = sendFcmV1($token, $title, $message, $data);
    echo json_encode(['status' => 'ok', 'fcm' => $resp], JSON_UNESCAPED_UNICODE);
} catch (Exception $e) {
    http_response_code(502);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()], JSON_UNESCAPED_UNICODE);
}
