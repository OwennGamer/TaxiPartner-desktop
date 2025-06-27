<?php
header("Content-Type: application/json");
require_once "db.php";

try {
    $stmt = $pdo->query("SELECT id, rejestracja, marka, model, przebieg, ubezpieczenie_do, przeglad_do, aktywny FROM pojazdy");
    $vehicles = $stmt->fetchAll(PDO::FETCH_ASSOC);
    echo json_encode(["status" => "success", "data" => $vehicles]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
?>
