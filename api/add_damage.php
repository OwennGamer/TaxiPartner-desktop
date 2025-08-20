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

$uploadDir = __DIR__ . '/uploads/damages/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$paths = [];
if (!empty($_FILES['photos']['name']) && is_array($_FILES['photos']['name'])) {
    $count = count($_FILES['photos']['name']);
    for ($i = 0; $i < $count; $i++) {
        if ($_FILES['photos']['error'][$i] === UPLOAD_ERR_OK) {
            $filename = uniqid('damage_') . '.jpg';
            $target = $uploadDir . $filename;
            if (move_uploaded_file($_FILES['photos']['tmp_name'][$i], $target)) {
                $paths[] = 'uploads/damages/' . $filename;
            }
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
