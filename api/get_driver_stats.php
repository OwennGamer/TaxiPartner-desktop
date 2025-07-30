<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    http_response_code(401);
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}
if ($decoded->role !== 'admin' && $decoded->role !== 'flotowiec') {
    http_response_code(403);
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nieprawidłowe żądanie"]);
    exit;
}

// Oczekiwany format dat: YYYY-MM-DD
$driverId  = $_GET['driver_id']  ?? '';
$startDate = $_GET['start_date'] ?? '';
$endDate   = $_GET['end_date']   ?? '';
if (!$driverId || !$startDate || !$endDate) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Brak wymaganych parametrów"]);
    exit;
}

$format = 'Y-m-d';
$startObj = DateTime::createFromFormat($format, $startDate);
$endObj = DateTime::createFromFormat($format, $endDate);
// Walidacja formatu daty i kolejności (start <= end)
if (!$startObj || $startObj->format($format) !== $startDate ||
    !$endObj || $endObj->format($format) !== $endDate ||
    $startObj > $endObj) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nieprawidłowe daty"]);
    exit;
}

try {
        // Suma kursów wg typu (voucher tylko gdy via_km = 0)
    $stmt = $pdo->prepare("
        SELECT
            SUM(CASE WHEN type = 'Voucher' AND via_km = 0 THEN amount ELSE 0 END) AS voucher,
            SUM(CASE WHEN type = 'Voucher' AND via_km = 1 THEN amount ELSE 0 END) AS voucher_km,
            SUM(CASE WHEN type = 'Karta' THEN amount ELSE 0 END) AS card,
            SUM(CASE WHEN type = 'Gotówka' THEN amount ELSE 0 END) AS cash
        FROM kursy
        WHERE driver_id = ? AND date BETWEEN ? AND ?
    ");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $voucher    = (float)$row['voucher'];
    $voucherKm  = (float)$row['voucher_km'];
    $card       = (float)$row['card'];
    $cash       = (float)$row['cash'];
    $turnover   = $voucher + $voucherKm + $card + $cash;

        // Kursy z lotniska + vouchery rozliczane za km
    $stmt = $pdo->prepare(
        "SELECT COALESCE(SUM(amount),0) FROM kursy WHERE driver_id = ? AND ((source = 'Lotnisko') OR (type = 'Voucher' AND via_km = 1)) AND date BETWEEN ? AND ?"
    );
    $stmt->execute([$driverId, $startDate, $endDate]);
    $lot = (float)$stmt->fetchColumn();

        // Sumaryczny przebieg z zakończonych sesji pracy
    $stmt = $pdo->prepare(
        "SELECT COALESCE(SUM(end_odometer - start_odometer),0)\n" .
        "  FROM work_sessions\n" .
        " WHERE driver_id = ?\n" .
        "   AND DATE(start_time) BETWEEN ? AND ?\n" .
        "   AND end_time IS NOT NULL\n" .
        "   AND end_odometer IS NOT NULL"
    );
    $stmt->execute([$driverId, $startDate, $endDate]);
    $kilometers = (float)$stmt->fetchColumn();
    $missingMileage = $kilometers == 0.0;

    // Suma paliwa
    $stmt = $pdo->prepare("SELECT COALESCE(SUM(cost),0) FROM refuels WHERE driver_id = ? AND refuel_date BETWEEN ? AND ?");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $fuelSum = (float)$stmt->fetchColumn();

    echo json_encode([
        "status" => "success",
        "data" => [
            "voucher"    => $voucher,
            "card"       => $card,
            "cash"       => $cash,
            "lot"        => $lot,
            "turnover"   => $turnover,
            "kilometers"     => $kilometers,
            "fuel_sum"       => $fuelSum,
            "missing_mileage" => $missingMileage
        ]
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
