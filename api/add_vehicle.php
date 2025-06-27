<?php
header("Content-Type: application/json");
require_once "db.php";

$required_fields = ['rejestracja', 'marka', 'model', 'przebieg', 'ubezpieczenie_do', 'przeglad_do', 'aktywny'];

foreach ($required_fields as $field) {
    if (!isset($_POST[$field])) {
        echo json_encode(["status" => "error", "message" => "Brak pola: $field"]);
        exit;
    }
}

$rejestracja = $_POST['rejestracja'];
$marka = $_POST['marka'];
$model = $_POST['model'];
$przebieg = intval($_POST['przebieg']);
$ubezpieczenie = $_POST['ubezpieczenie_do'];
$przeglad = $_POST['przeglad_do'];
$aktywny = intval($_POST['aktywny']);

try {
    $stmt = $pdo->prepare("INSERT INTO pojazdy (rejestracja, marka, model, przebieg, ubezpieczenie_do, przeglad_do, aktywny)
                           VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->execute([$rejestracja, $marka, $model, $przebieg, $ubezpieczenie, $przeglad, $aktywny]);

    echo json_encode(["status" => "success", "message" => "Pojazd dodany pomyślnie"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych: " . $e->getMessage()]);
}
?>
