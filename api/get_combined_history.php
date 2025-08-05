<?php
header("Content-Type: application/json");

// Połączenie z bazą danych
require_once "db.php";

// 🔵 Start logowania
file_put_contents("debug_log.txt", "🔵 Skrypt get_combined_history.php startuje\n", FILE_APPEND);

// Odczyt danych POST
$data = json_decode(file_get_contents("php://input"), true);
if (!isset($data['driverId'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit;
}

$driverId = $data['driverId'];

try {
    $result = [];

    // Kursy
    $stmt = $pdo->prepare("
        SELECT date, 'Kurs' as type, CONCAT(source, ' ', type, ' ', amount, ' zł') as description, saldo_wplyw AS change_value, saldo_po, receipt_photo
        FROM kursy
        WHERE driver_id = ? 
        ORDER BY date DESC
    ");
    $stmt->execute([$driverId]);
    $courses = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($courses as $row) {
        $path = $row['receipt_photo'];
        $exists = $path && file_exists(__DIR__ . '/' . $path);
        $result[] = [
            "date" => $row['date'],
            "type" => $row['type'],
            "description" => $row['description'],
            "change" => $row['change_value'],
            "saldo_po" => $row['saldo_po'],
            "receipt_photo" => $exists ? $path : null,
            "photo_available" => $exists
        ];
    }

    // Zmiany salda
    $stmt = $pdo->prepare("
        SELECT data as date, 'Zmiana salda' as type, powod as description, zmiana AS change_value, saldo_po 
        FROM historia_salda 
        WHERE kierowca_id = ? 
        ORDER BY data DESC
    ");
    $stmt->execute([$driverId]);
    $saldo_changes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($saldo_changes as $row) {
        $result[] = [
            "date" => $row['date'],
            "type" => $row['type'],
            "description" => $row['description'],
            "change" => $row['change_value'],
            "saldo_po" => $row['saldo_po'],
            "receipt_photo" => null,
            "photo_available" => false
        ];
    }

    // 🔥 Sortowanie po dacie malejąco
    usort($result, function($a, $b) {
        return strtotime($b['date']) - strtotime($a['date']);
    });

    echo json_encode(["status" => "success", "data" => $result]);
    exit;

} catch (Exception $e) {
    echo json_encode(["status" => "error", "message" => "Błąd: " . $e->getMessage()]);
    exit;
}
?>
