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
if (!empty($_FILES['photos']) && isset($_FILES['photos']['name']) && is_array($_FILES['photos']['name'])) {
    foreach ($_FILES['photos']['name'] as $idx => $originalName) {
        if ($_FILES['photos']['error'][$idx] === UPLOAD_ERR_OK) {
            $filename = uniqid('serv_') . '.jpg';
            $target = $uploadDir . $filename;
            if (move_uploaded_file($_FILES['photos']['tmp_name'][$idx], $target)) {
                $paths[] = 'uploads/service/' . $filename;
            }
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
