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

if ($id <= 0 || $opis === '' || $koszt === null) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

$uploadDir = __DIR__ . '/uploads/service/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0777, true);
}

$newPaths = [];
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

$files = $_FILES['photos'] ?? [];
$files = isset($files['name']) ? normalizeFilesArray($files) : [];

foreach ($files as $file) {
    if ($file['error'] === UPLOAD_ERR_OK) {
        $filename = uniqid('serv_') . '.jpg';
        $target = $uploadDir . $filename;
        if (move_uploaded_file($file['tmp_name'], $target)) {
            $newPaths[] = 'uploads/service/' . $filename;
        }
    }
}

$remove = $_POST['remove_photos'] ?? [];
if (!is_array($remove)) {
    $remove = [$remove];
}

try {
    $stmt = $pdo->prepare('SELECT zdjecia FROM serwisy WHERE id = :id');
    $stmt->execute([':id' => $id]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $paths = $row && $row['zdjecia'] ? json_decode($row['zdjecia'], true) : [];
    $deleted = [];

    foreach ($remove as $r) {
        $r = trim($r);
        $file = null;
        if ($r === '') {
            continue;
        }
        if (is_numeric($r)) {
            $idx = (int)$r;
            if (isset($paths[$idx])) {
                $file = $paths[$idx];
                unset($paths[$idx]);
            }
        } else {
            $needle = $r;
            if (!in_array($needle, $paths, true)) {
                $parsed = parse_url($r, PHP_URL_PATH);
                $needle = $parsed ? ltrim($parsed, '/') : $needle;
            }
            $idx = array_search($needle, $paths, true);
            if ($idx !== false) {
                $file = $paths[$idx];
                unset($paths[$idx]);
            }
        }
        if ($file) {
            $deleted[] = $file;
        }
    }

    $paths = array_values($paths);
    $paths = array_merge($paths, $newPaths);

    $upd = $pdo->prepare('UPDATE serwisy SET opis=:op, koszt=:ko, zdjecia=:zdj WHERE id=:id');
    $upd->execute([
        ':op' => $opis,
        ':ko' => $koszt,
        ':zdj' => json_encode($paths),
        ':id' => $id
    ]);

    foreach ($deleted as $file) {
        $full = __DIR__ . '/' . $file;
        if (is_file($full)) {
            @unlink($full);
        }
    }

    echo json_encode(['status' => 'success']);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
