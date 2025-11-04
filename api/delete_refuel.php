<?php
// delete_refuel.php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/db.php';

// Autoryzacja
$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    http_response_code(401);
    echo json_encode(['status'=>'error','message'=>'Brak tokena']);
    exit;
}
$role = strtolower($decoded->role ?? '');
if (!in_array($role, ['admin', 'administrator', 'flotowiec'], true)) {
    http_response_code(403);
    echo json_encode(['status'=>'error','message'=>'Brak uprawnień']);
    exit;
}

// Parametr id
$id = isset($_GET['id']) ? intval($_GET['id']) : 0;
if ($id <= 0) {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Nieprawidłowe id']);
    exit;
}

try {
    $stmt = $pdo->prepare("DELETE FROM refuels WHERE id = ?");
    $stmt->execute([$id]);

    echo json_encode([
        'status'  => 'success',
        'message' => 'Wpis usunięty'
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status'=>'error','message'=>'Błąd bazy danych']);
}
