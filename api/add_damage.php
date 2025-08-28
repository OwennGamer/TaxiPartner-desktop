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

$rejestracja = trim($_POST['rejestracja'] ?? '');
$nr_szkody = trim($_POST['nr_szkody'] ?? '');
$opis = trim($_POST['opis'] ?? '');
$status = trim($_POST['status'] ?? '');

if ($rejestracja === '' || $nr_szkody === '' || $opis === '' || $status === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

function normalizeFilesArray(array $files): array {
    if (!is_array($files['name'])) {
        return [$files];
    }
    $normalized = [];
    foreach ($files['name'] as $index => $name) {
        $normalized[] = [
            'name' => $name,
            'type' => $files['type'][$index],
            'tmp_name' => $files['tmp_name'][$index],
            'error' => $files['error'][$index],
            'size' => $files['size'][$index],
        ];
    }
    return $normalized;
}

$uploadDir = __DIR__ . '/uploads/damages/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$paths = [];
$files = $_FILES['photos'] ?? $_FILES['photos'] ?? [];
$files = isset($files['name']) ? normalizeFilesArray($files) : [];
foreach ($files as $file) {
    if ($file['error'] === UPLOAD_ERR_OK) {
        $filename = uniqid('damage_') . '.jpg';
        $target = $uploadDir . $filename;
        error_log('FILES: ' . print_r($_FILES, true));
        if (move_uploaded_file($file['tmp_name'], $target)) {
            $paths[] = 'uploads/damages/' . $filename;
        } else {
            error_log('MOVE ERROR: ' . print_r(error_get_last(), true));
        }
    }
}

try {
    $stmt = $pdo->prepare("INSERT INTO szkody (rejestracja, nr_szkody, opis, status, zdjecia) VALUES (:re, :nr, :op, :st, :zdj)");
    $stmt->execute([
        ':re' => $rejestracja,
        ':nr' => $nr_szkody,
        ':op' => $opis,
        ':st' => $status,
        ':zdj' => json_encode($paths)
    ]);
    echo json_encode(['status' => 'success', 'id' => $pdo->lastInsertId()]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
