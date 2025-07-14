<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
if (!$token || !verifyJWT($token)) {
    http_response_code(401);
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}
$decoded = verifyJWT($token);
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

$driverId  = $_GET['driver_id']  ?? '';
$startDate = $_GET['start_date'] ?? '';
$endDate   = $_GET['end_date']   ?? '';
if (!$driverId || !$startDate || !$endDate) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Brak wymaganych parametrów"]);
    exit;
}

try {
    // Suma kursów wg typu
    $stmt = $pdo->prepare("SELECT type, SUM(amount) AS total FROM kursy WHERE driver_id = ? AND date BETWEEN ? AND ? GROUP BY type");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $voucher = $card = $cash = 0.0;
    foreach ($stmt->fetchAll(PDO::FETCH_ASSOC) as $row) {
        $sum = (float)$row['total'];
        if ($row['type'] === 'Voucher') {
            $voucher = $sum;
        } elseif ($row['type'] === 'Karta') {
            $card = $sum;
        } elseif ($row['type'] === 'Gotówka') {
            $cash = $sum;
        }
    }
    $turnover = $voucher + $card + $cash;

    // Kursy z lotniska (jeśli występują)
    $stmt = $pdo->prepare("SELECT COALESCE(SUM(amount),0) FROM kursy WHERE driver_id = ? AND source = 'Lotnisko' AND date BETWEEN ? AND ?");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $lot = (float)$stmt->fetchColumn();

    // Kilometry z inwentaryzacji
    $stmt = $pdo->prepare("SELECT przebieg FROM inwentaryzacje WHERE kierowca_id = ? AND data_dodania >= ? AND data_dodania <= ? ORDER BY data_dodania ASC LIMIT 1");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $startMileage = $stmt->fetchColumn();
    $stmt = $pdo->prepare("SELECT przebieg FROM inwentaryzacje WHERE kierowca_id = ? AND data_dodania >= ? AND data_dodania <= ? ORDER BY data_dodania DESC LIMIT 1");
    $stmt->execute([$driverId, $startDate, $endDate]);
    $endMileage = $stmt->fetchColumn();
    $kilometers = 0.0;
    if ($startMileage !== false && $endMileage !== false) {
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
            "kilometers" => $kilometers,
            "fuel_sum"   => $fuelSum
        ]
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
