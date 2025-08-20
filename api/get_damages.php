<?php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/jwt_utils.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
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

$rejestracja = trim($_GET['rejestracja'] ?? '');
if ($rejestracja === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak rejestracji']);
    exit;
}

try {
    $stmt = $pdo->prepare("SELECT id, rejestracja, nr_szkody, opis, status, zdjecia, data FROM szkody WHERE rejestracja = :re ORDER BY data DESC");
    $stmt->execute([':re' => $rejestracja]);
    $damages = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $row['zdjecia'] = $row['zdjecia'] ? json_decode($row['zdjecia'], true) : [];
        $damages[] = $row;
    }
    echo json_encode(['status' => 'success', 'damages' => $damages]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
