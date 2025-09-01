<?php
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/db.php';

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    http_response_code(401);
    echo json_encode(['status' => 'error', 'message' => 'Brak ważnego tokena']);
    exit;
}

if (($decoded->role ?? '') !== 'admin') {
    http_response_code(403);
    echo json_encode(['status' => 'error', 'message' => 'Brak uprawnień']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$driverId = $input['id'] ?? '';
if ($driverId === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak ID kierowcy']);
    exit;
}

function tableExists(PDO $pdo, string $table): bool {
    $stmt = $pdo->prepare('SHOW TABLES LIKE ?');
    $stmt->execute([$table]);
    return $stmt->fetchColumn() !== false;
}

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare('UPDATE kierowcy SET fcm_token = NULL WHERE id = ?');
    $stmt->execute([$driverId]);

    $tables = ['jwt_tokens', 'driver_sessions', 'sessions'];
    foreach ($tables as $tbl) {
        if (tableExists($pdo, $tbl)) {
            $del = $pdo->prepare("DELETE FROM {$tbl} WHERE driver_id = ?");
            $del->execute([$driverId]);
        }
    }

    $pdo->commit();
    echo json_encode(['status' => 'success']);
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
