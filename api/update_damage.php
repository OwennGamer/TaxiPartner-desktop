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
$koszt = isset($_POST['koszt']) ? floatval($_POST['koszt']) : null;
$status = trim($_POST['status'] ?? '');

if ($id <= 0 || $opis === '' || $koszt === null || $status === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

$uploadDir = __DIR__ . '/uploads/damages/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$newPaths = [];
if (!empty($_FILES['photos']['name']) && is_array($_FILES['photos']['name'])) {
    $count = count($_FILES['photos']['name']);
    for ($i = 0; $i < $count; $i++) {
        if ($_FILES['photos']['error'][$i] === UPLOAD_ERR_OK) {
            $filename = uniqid('damage_') . '.jpg';
            $target = $uploadDir . $filename;
            if (move_uploaded_file($_FILES['photos']['tmp_name'][$i], $target)) {
                $newPaths[] = 'uploads/damages/' . $filename;
            }
        }
    }
}

try {
    $stmt = $pdo->prepare('SELECT zdjecia FROM szkody WHERE id = :id');
    $stmt->execute([':id' => $id]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $paths = $row && $row['zdjecia'] ? json_decode($row['zdjecia'], true) : [];
    $paths = array_merge($paths, $newPaths);

    $upd = $pdo->prepare('UPDATE szkody SET opis=:op, koszt=:ko, status=:st, zdjecia=:zdj WHERE id=:id');
    $upd->execute([
        ':op' => $opis,
        ':ko' => $koszt,
        ':st' => $status,
        ':zdj' => json_encode($paths),
        ':id' => $id
    ]);

    echo json_encode(['status' => 'success']);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
