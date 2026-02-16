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
    $raw = $ride['receipt_photo'] ?? null;
    $paths = [];
    if (is_string($raw) && $raw !== '') {
        $decoded = json_decode($raw, true);
        if (is_array($decoded)) {
            foreach ($decoded as $path) {
                if (is_string($path) && $path !== '' && file_exists(__DIR__ . '/' . $path)) {
                    $paths[] = $path;
                }
            }
        } elseif (file_exists(__DIR__ . '/' . $raw)) {
            $paths[] = $raw;
        }
    }
    $ride['receipt_photos'] = $paths;
    $ride['photo_available'] = !empty($paths);
    $ride['receipt_photo'] = $paths[0] ?? null;
}
unset($ride);


if ($rides) {
    echo json_encode(["status" => "success", "data" => $rides]);
} else {
    echo json_encode(["status" => "error", "message" => "Brak historii kursów"]);
}
?>
