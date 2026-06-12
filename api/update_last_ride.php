<?php
header("Content-Type: application/json");

require_once "db.php";
require_once "auth.php";
require_once __DIR__ . "/voucher_utils.php";
require_once __DIR__ . "/ride_calculation.php";

if (!isset($_POST['driver_id'], $_POST['amount'], $_POST['type'], $_POST['source'])) {
    echo json_encode(["status" => "error", "message" => "Brak danych wejściowych"]);
    exit;
}

$driver_id = $_POST['driver_id'];
$amount = (float)$_POST['amount'];
$type = $_POST['type'];
$source = $_POST['source'];
$via_km = isset($_POST['via_km']) ? (int)$_POST['via_km'] : 0;

function calculate_final_amount(float $amount, string $type, string $source, array $terms): float {
    return calculate_ride_saldo_impact($amount, $type, $source, $terms);
}

try {
    $pdo->beginTransaction();

    $stmt = $pdo->prepare("SELECT id, saldo, voucher_current_amount, voucher_current_month, voucher_previous_amount, voucher_previous_month FROM kierowcy WHERE id = ? FOR UPDATE");
    $stmt->execute([$driver_id]);
    $driver = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$driver) {
        $pdo->rollBack();
        echo json_encode(["status" => "error", "message" => "Nie znaleziono kierowcy"]);
        exit;
    }

    $driver = voucher_refresh_buckets($pdo, $driver);

    $stmtTerms = $pdo->prepare("SELECT term_name, term_value FROM collaboration_terms WHERE driver_id = ?");
    $stmtTerms->execute([$driver_id]);
    $terms = $stmtTerms->fetchAll(PDO::FETCH_KEY_PAIR);

    $lastRideStmt = $pdo->prepare(
        "SELECT id, amount, type, source, via_km, date
         FROM kursy
         WHERE driver_id = ?
         ORDER BY date DESC, id DESC
         LIMIT 1
         FOR UPDATE"
    );
    $lastRideStmt->execute([$driver_id]);
    $lastRide = $lastRideStmt->fetch(PDO::FETCH_ASSOC);

    if (!$lastRide) {
        $pdo->rollBack();
        echo json_encode(["status" => "error", "message" => "Brak kursu do edycji"]);
        exit;
    }

    $oldAmount = (float)$lastRide['amount'];
    $oldType = $lastRide['type'];
    $oldSource = $lastRide['source'];

    $oldFinal = calculate_final_amount($oldAmount, $oldType, $oldSource, $terms);
    $newFinal = calculate_final_amount($amount, $type, $source, $terms);

    $currentSaldo = (float)$driver['saldo'];
    $oldSaldoImpact = strtolower($oldType) !== 'voucher' ? $oldFinal : 0.0;
    $newSaldoImpact = strtolower($type) !== 'voucher' ? $newFinal : 0.0;
    $newSaldo = round($currentSaldo - $oldSaldoImpact + $newSaldoImpact, 2);

    if (strtolower($oldType) === 'voucher') {
        $rideMonth = (new DateTime($lastRide['date']))->format('Y-m');
        $bucket = voucher_bucket_for_month($driver, $rideMonth);
        $driver = voucher_increment_bucket($pdo, $driver_id, $driver, -$oldFinal, $bucket);
    }

    if (strtolower($type) === 'voucher') {
        $rideMonth = (new DateTime($lastRide['date']))->format('Y-m');
        $bucket = voucher_bucket_for_month($driver, $rideMonth);
        $driver = voucher_increment_bucket($pdo, $driver_id, $driver, $newFinal, $bucket);
    }

    $updateRideStmt = $pdo->prepare("UPDATE kursy SET amount = ?, saldo_wplyw = ?, saldo_po = ?, type = ?, source = ?, via_km = ? WHERE id = ?");
    $updateRideStmt->execute([
        $amount,
        $newFinal,
        $newSaldo,
        $type,
        $source,
        $via_km,
        $lastRide['id']
    ]);

    $updateDriverStmt = $pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?");
    $updateDriverStmt->execute([$newSaldo, $driver_id]);

    $pdo->commit();

    echo json_encode(["status" => "success", "message" => "Ostatni kurs został zaktualizowany"]);
    exit;
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(["status" => "error", "message" => "Błąd: " . $e->getMessage()]);
    exit;
}
