<?php
require_once 'auth.php';
require_once 'db.php';
require_once __DIR__ . '/voucher_utils.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}

if ($decoded->role !== 'admin' && $decoded->role !== 'flotowiec') {
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

try {
    // Pobieramy wszystkich kierowców + vehiclePlate + sumę kosztów paliwa
    $stmt = $pdo->prepare("
        SELECT
          k.*,
          k.last_vehicle_plate AS vehiclePlate,
          COALESCE((
            SELECT SUM(cost)
              FROM refuels
             WHERE driver_id = k.id
          ), 0) AS fuelCostSum
        FROM kierowcy k
    ");
    $stmt->execute();
    $drivers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($drivers as &$driver) {
        $driver = voucher_refresh_buckets($pdo, $driver);
        // Pobierz warunki współpracy
        $termsStmt = $pdo->prepare("
            SELECT term_name, term_value
              FROM collaboration_terms
             WHERE driver_id = ?
        ");
        $termsStmt->execute([$driver['id']]);
        $terms = $termsStmt->fetchAll(PDO::FETCH_ASSOC);

        foreach ($terms as $term) {
            $driver[$term['term_name']] = $term['term_value'];
        }
    }

    echo json_encode([
        "status"  => "success",
        "drivers" => $drivers
    ]);

} catch (PDOException $e) {
    echo json_encode([
        "status"  => "error",
        "message" => "Błąd pobierania danych: " . $e->getMessage()
    ]);
}
