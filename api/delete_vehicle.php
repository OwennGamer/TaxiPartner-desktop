<?php
header('Content-Type: application/json');
require_once 'db.php';

$id = isset($_POST['id']) ? intval($_POST['id']) : 0;
if ($id <= 0) {
    echo json_encode(["status" => "error", "message" => "Brak parametru id"]);
    exit;
}

try {
    $stmt = $pdo->prepare("DELETE FROM pojazdy WHERE id = ?");
    $stmt->execute([$id]);
    echo json_encode(["status" => "success", "message" => "Pojazd usunięty"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
?>
