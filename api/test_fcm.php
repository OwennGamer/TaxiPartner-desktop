<?php
require_once __DIR__ . '/config.php';

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$token = trim($input['token'] ?? '');
$message = trim($input['message'] ?? '');

if ($token === '' || $message === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak tokenu lub wiadomości']);
    exit;
}

if (!defined('FCM_SERVER_KEY') || FCM_SERVER_KEY === '') {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Brak FCM_SERVER_KEY']);
    exit;
}

$payload = [
    'to' => $token,
    'notification' => [
        'title' => 'Test',
        'body' => $message
    ]
];

$headers = [
    'Authorization: key=' . FCM_SERVER_KEY,
    'Content-Type: application/json'
];

$ch = curl_init('https://fcm.googleapis.com/fcm/send');
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));

$response = curl_exec($ch);
if ($response === false) {
    $error = curl_error($ch);
    curl_close($ch);
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'FCM send error: ' . $error]);
    exit;
}

$status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

http_response_code(200);
echo json_encode(['fcm_status' => $status, 'fcm_response' => $response]);
