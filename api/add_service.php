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
$opis = trim($_POST['opis'] ?? '');
$koszt = isset($_POST['koszt']) ? floatval($_POST['koszt']) : null;

if ($rejestracja === '' || $opis === '' || $koszt === null) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

$uploadDir = __DIR__ . '/uploads/service/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$paths = [];
function normalizeFilesArray(array $files): array {
    if (!is_array($files['name'])) {
        $files = [
            'name' => [$files['name']],
            'type' => [$files['type']],
            'tmp_name' => [$files['tmp_name']],
            'error' => [$files['error']],
            'size' => [$files['size']],
        ];
    }
        $normalized = [];
    foreach ($files['name'] as $i => $name) {
        $normalized[] = [
            'name' => $name,
            'type' => $files['type'][$i],
            'tmp_name' => $files['tmp_name'][$i],
            'error' => $files['error'][$i],
            'size' => $files['size'][$i],
        ];
    }
    return $normalized;
}
$files = $_FILES['photos'] ?? $_FILES['photos'] ?? [];
$files = isset($files['name']) ? normalizeFilesArray($files) : [];
foreach ($files as $file) {
    if ($file['error'] === UPLOAD_ERR_OK) {
        $filename = uniqid('serv_') . '.jpg';
        $target = $uploadDir . $filename;
        error_log('FILES: ' . print_r($_FILES, true));
        if (move_uploaded_file($file['tmp_name'], $target)) {
            $paths[] = 'uploads/service/' . $filename;
        } else {
            error_log('MOVE ERROR: ' . print_r(error_get_last(), true));
        }
    }
}

try {
    $stmt = $pdo->prepare("INSERT INTO serwisy (rejestracja, opis, koszt, zdjecia) VALUES (:re, :op, :ko, :zdj)");
    $stmt->execute([
        ':re' => $rejestracja,
        ':op' => $opis,
        ':ko' => $koszt,
        ':zdj' => json_encode($paths)
    ]);
    echo json_encode(['status' => 'success', 'id' => $pdo->lastInsertId()]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
