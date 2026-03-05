<?php
header("Content-Type: application/json");
require_once "db.php";

if (!isset($_GET['driver_id'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit;
}

$driver_id = $_GET['driver_id'];

$stmt = $pdo->prepare("
    (
    SELECT
      date        AS date,
      source      AS source,
      type        AS type,
      CAST(amount AS CHAR) AS amount,
      receipt_photo,
      1           AS is_ride
    FROM kursy
    WHERE driver_id = ?
  )
  UNION ALL
  (
    SELECT
      data        AS date,
      'zmiana salda' AS source,
      ''          AS type,
      CAST(zmiana AS CHAR) AS amount,
      NULL        AS receipt_photo,
      0           AS is_ride
    FROM historia_salda
    WHERE kierowca_id = ?
      AND COALESCE(counter_type, 'saldo') = 'saldo'
  )
  ORDER BY date DESC
");

$stmt->execute([$driver_id, $driver_id]);
$history = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($history as &$entry) {
    $raw = $entry['receipt_photo'] ?? null;
    $paths = [];
    if ((int)($entry['is_ride'] ?? 0) === 1 && is_string($raw) && $raw !== '') {
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
    $entry['receipt_photos'] = $paths;
    $entry['photo_available'] = !empty($paths);
    $entry['receipt_photo'] = $paths[0] ?? null;
}
unset($entry);


if ($history) {
    echo json_encode(["status" => "success", "data" => $history]);
} else {
    echo json_encode(["status" => "error", "message" => "Brak historii kursów"]);
}
?>
