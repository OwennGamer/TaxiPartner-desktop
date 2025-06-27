<?php
// add_refuel.php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';       // getAuthorizationHeader() i verifyJWT()
require_once __DIR__ . '/jwt_utils.php';


// metoda musi być POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

// AUTORYZACJA: pobranie i weryfikacja JWT z nagłówka Authorization
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

// ID kierowcy z tokena
$driver_id = $decoded->user_id;

// Pobranie i walidacja pól
$fuel_amount = isset($_POST['fuel_amount']) ? floatval($_POST['fuel_amount']) : null;
$cost        = isset($_POST['cost'])        ? floatval($_POST['cost'])        : null;
$odometer    = isset($_POST['odometer'])    ? intval($_POST['odometer'])      : null;

if ($fuel_amount === null || $fuel_amount <= 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowa ilość paliwa']);
    exit;
}
if ($cost === null || $cost <= 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowy koszt']);
    exit;
}
if ($odometer === null || $odometer < 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowy przebieg']);
    exit;
}

try {
    // Wstawienie rekordu do tabeli refuels
    $sql = "INSERT INTO refuels
      (driver_id, refuel_date, fuel_amount, cost, odometer)
     VALUES
      (:driver, NOW(), :amount, :cost, :odo)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':driver' => $driver_id,
        ':amount' => $fuel_amount,
        ':cost'   => $cost,
        ':odo'    => $odometer
    ]);

    echo json_encode([
        'status' => 'success',
        'message' => 'Tankowanie zapisane',
        'id' => $pdo->lastInsertId()
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
