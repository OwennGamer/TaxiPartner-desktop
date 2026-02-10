<?php
header("Content-Type: application/json");

require_once "db.php";
require_once "auth.php";

if (!isset($_GET['driver_id'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit;
}

$driver_id = $_GET['driver_id'];

$stmt = $pdo->prepare(
    "SELECT id, date, source, type, amount, via_km
     FROM kursy
     WHERE driver_id = ?
     ORDER BY date DESC, id DESC
     LIMIT 1"
);
$stmt->execute([$driver_id]);
$ride = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$ride) {
    echo json_encode(["status" => "error", "message" => "Brak kursów do edycji"]);
    exit;
}

echo json_encode([
    "status" => "success",
    "data" => [
        "id" => (int)$ride['id'],
        "date" => $ride['date'],
        "source" => $ride['source'],
        "type" => $ride['type'],
        "amount" => (string)$ride['amount'],
        "via_km" => (int)$ride['via_km']
    ]
]);
