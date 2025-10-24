<?php
header("Content-Type: application/json");
require_once "db.php";
require_once __DIR__ . "/voucher_utils.php";

if (!isset($_GET['driver_id'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit;
}

$driver_id = $_GET['driver_id'];

$stmt = $pdo->prepare("SELECT id, imie, nazwisko, saldo, voucher_current_amount, voucher_current_month, voucher_previous_amount, voucher_previous_month FROM kierowcy WHERE id = ?");
$stmt->execute([$driver_id]);
$driver = $stmt->fetch(PDO::FETCH_ASSOC);

if ($driver) {
    $driver = voucher_refresh_buckets($pdo, $driver);

    echo json_encode([
        "status" => "success",
        "data" => [
            "id"                      => $driver['id'],
            "imie"                    => $driver['imie'],
            "nazwisko"                => $driver['nazwisko'],
            "saldo"                   => (float)$driver['saldo'],
            "voucher_current_amount"  => isset($driver['voucher_current_amount']) ? (float)$driver['voucher_current_amount'] : 0.0,
            "voucher_current_month"   => $driver['voucher_current_month'] ?? null,
            "voucher_previous_amount" => isset($driver['voucher_previous_amount']) ? (float)$driver['voucher_previous_amount'] : 0.0,
            "voucher_previous_month"  => $driver['voucher_previous_month'] ?? null,
        ]
    ]);
} else {
    echo json_encode(["status" => "error", "message" => "Nie znaleziono kierowcy"]);
}
?>
