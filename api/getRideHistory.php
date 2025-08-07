<?php
header("Content-Type: application/json");
require_once "db.php";

if (!isset($_GET['driver_id'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit;
}

$driver_id = $_GET['driver_id'];

$stmt = $pdo->prepare("
  SELECT
    date        AS date,
    source      AS source,
    type        AS type,
    amount      AS amount,
    receipt_photo
  FROM kursy
  WHERE driver_id = ?
  ORDER BY date DESC
");

$stmt->execute([$driver_id]);
$rides = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($rides as &$ride) {
    $path = $ride['receipt_photo'];
    $exists = $path && file_exists(__DIR__ . '/' . $path);
    $ride['photo_available'] = $exists;
    $ride['receipt_photo'] = $exists ? $path : null;
}
unset($ride);


if ($rides) {
    echo json_encode(["status" => "success", "data" => $rides]);
} else {
    echo json_encode(["status" => "error", "message" => "Brak historii kursów"]);
}
?>
