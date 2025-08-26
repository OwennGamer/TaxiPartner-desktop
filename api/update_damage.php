<?php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/jwt_utils.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$token = getAuthorizationHeader();
if (!$token) {
    http_response_code(401);
    echo json_encode(['status' => 'error', 'message' => 'Brak tokena']);
    exit;
}
$decoded = verifyJWT($token);
if (!$decoded) {
    http_response_code(401);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowy token']);
    exit;
}

$id = isset($_POST['id']) ? intval($_POST['id']) : 0;
$opis = trim($_POST['opis'] ?? '');
$status = trim($_POST['status'] ?? '');

if ($id <= 0 || $opis === '' || $status === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

try {
    $upd = $pdo->prepare('UPDATE szkody SET opis=:op, status=:st WHERE id=:id');
    $upd->execute([
        ':op' => $opis,
        ':st' => $status,
        ':id' => $id
    ]);

    echo json_encode(['status' => 'success']);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
