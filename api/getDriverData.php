<?php
header("Content-Type: application/json");
require_once "db.php";

if (!isset($_GET['driver_id'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit;
}

$driver_id = $_GET['driver_id'];

$stmt = $pdo->prepare("SELECT id, imie, nazwisko, saldo FROM kierowcy WHERE id = ?");
$stmt->execute([$driver_id]);
$driver = $stmt->fetch(PDO::FETCH_ASSOC);

if ($driver) {
    echo json_encode(["status" => "success", "data" => $driver]);
} else {
    echo json_encode(["status" => "error", "message" => "Nie znaleziono kierowcy"]);
}
?>
