<?php
// get_refuels.php
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

// Parametr driver_id
$driverId = $_GET['driver_id'] ?? '';
if (!$driverId) {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Brakuje driver_id']);
    exit;
}

try {
    $stmt = $pdo->prepare("
        SELECT
            id,
            DATE_FORMAT(refuel_date, '%Y-%m-%d %H:%i:%s') AS refuel_date,
            fuel_amount,
            cost,
            odometer
        FROM refuels
        WHERE driver_id = ?
        ORDER BY refuel_date DESC
    ");
    $stmt->execute([$driverId]);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status'   => 'success',
        'refuels'  => $rows
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status'=>'error','message'=>'Błąd bazy danych']);
}
