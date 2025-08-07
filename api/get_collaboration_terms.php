<?php
require_once '/var/www/html/api/db.php'; // Ścieżka dostosowana do Twojej struktury katalogów

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(["status" => "error", "message" => "Nieprawidłowa metoda żądania"]);
    exit();
}

if (!isset($_GET['driver_id'])) {
    echo json_encode(["status" => "error", "message" => "Brak ID kierowcy"]);
    exit();
}

$driver_id = $_GET['driver_id'];

try {
    $stmt = $pdo->prepare("SELECT term_name, term_value FROM collaboration_terms WHERE driver_id = ?");
    $stmt->execute([$driver_id]);

    $terms = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $terms[$row['term_name']] = $row['term_value'];
    }

    if (!empty($terms)) {
        echo json_encode(["status" => "success", "terms" => $terms]);
    } else {
        echo json_encode(["status" => "error", "message" => "Nie znaleziono warunków współpracy"]);
    }
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych: " . $e->getMessage()]);
}
?>
