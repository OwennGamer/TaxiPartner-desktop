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
            SUM(CASE WHEN type = 'Karta' THEN amount ELSE 0 END) AS card,
            SUM(CASE WHEN type = 'Gotówka' THEN amount ELSE 0 END) AS cash
        FROM kursy
        WHERE driver_id = ? AND date BETWEEN ? AND ?
    ");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $voucher = (float)$row['voucher'];
    $card    = (float)$row['card'];
    $cash    = (float)$row['cash'];
    $turnover = $voucher + $card + $cash;

        // Kursy z lotniska + vouchery rozliczane za km
    $stmt = $pdo->prepare(
        "SELECT COALESCE(SUM(amount),0) FROM kursy WHERE driver_id = ? AND ((source = 'Lotnisko') OR (type = 'Voucher' AND via_km = 1)) AND date BETWEEN ? AND ?"
    );
    $stmt->execute([$driverId, $startDate, $endDate]);
    $lot = (float)$stmt->fetchColumn();

    // Kilometry z inwentaryzacji
    $stmt = $pdo->prepare("SELECT przebieg FROM inwentaryzacje WHERE kierowca_id = ? AND data_dodania >= ? AND data_dodania <= ? ORDER BY data_dodania ASC LIMIT 1");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $startMileage = $stmt->fetchColumn();
    $stmt = $pdo->prepare("SELECT przebieg FROM inwentaryzacje WHERE kierowca_id = ? AND data_dodania >= ? AND data_dodania <= ? ORDER BY data_dodania DESC LIMIT 1");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $endMileage = $stmt->fetchColumn();

    $stmt = $pdo->prepare("SELECT COUNT(*) FROM inwentaryzacje WHERE kierowca_id = ? AND data_dodania >= ? AND data_dodania <= ?");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $mileageCount = (int)$stmt->fetchColumn();
    $missingMileage = $mileageCount < 2;
    $kilometers = 0.0;
    if (!$missingMileage && $startMileage !== false && $endMileage !== false) {
        $kilometers = max(0, (int)$endMileage - (int)$startMileage);
    }

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
