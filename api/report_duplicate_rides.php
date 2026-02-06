<?php
header("Content-Type: application/json");

require_once "db.php";

$stmt = $pdo->prepare(
    "SELECT
        k1.id AS ride_id,
        k1.driver_id AS driver_id,
        k1.amount AS amount,
        k1.type AS type,
        k1.source AS source,
        k1.date AS date,
        k2.id AS matched_ride_id,
        k2.date AS matched_date,
        TIMESTAMPDIFF(SECOND, k1.date, k2.date) AS diff_seconds
    FROM kursy k1
    JOIN kursy k2
        ON k1.driver_id = k2.driver_id
        AND k1.amount = k2.amount
        AND k2.date > k1.date
        AND k2.date <= k1.date + INTERVAL 5 MINUTE
    ORDER BY k1.driver_id, k1.date"
);

$stmt->execute();
$rides = $stmt->fetchAll(PDO::FETCH_ASSOC);

if ($rides) {
    echo json_encode(["status" => "success", "data" => $rides]);
} else {
    echo json_encode(["status" => "error", "message" => "Brak kursów spełniających kryteria raportu"]);
}
?>
