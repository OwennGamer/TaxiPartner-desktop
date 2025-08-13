<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/fcm.php';

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$token = trim($input['token'] ?? '');
$messageText = trim($input['message'] ?? '');

if ($token === '' || $messageText === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak tokenu lub wiadomości']);
    exit;
}

$payload = [
    'token' => $token,
    'notification' => [
        'title' => 'Test',
        'body' => $messageText
    ]
];

try {
    $response = sendFcmMessage(FIREBASE_PROJECT_ID, $payload);
    http_response_code(200);
    echo json_encode([
        'fcm_status' => $response['statusCode'],
        'fcm_response' => $response['body']
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'FCM send error: ' . $e->getMessage()]);
}
