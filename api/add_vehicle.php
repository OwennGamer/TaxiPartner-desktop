<?php
header("Content-Type: application/json");
require_once "db.php";

$required_fields = [
    'rejestracja', 'marka', 'model', 'przebieg', 'ubezpieczenie_do',
    'przeglad_do', 'aktywny', 'inpost', 'taxi', 'taksometr',
    'legalizacja_taksometru_do', 'gaz', 'homologacja_lpg_do',
    'firma', 'forma_wlasnosci', 'numer_polisy'
];

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
$inpost = intval($_POST['inpost']);
$taxi = intval($_POST['taxi']);
$taksometr = intval($_POST['taksometr']);
$legalizacja = $_POST['legalizacja_taksometru_do'] !== '' ? $_POST['legalizacja_taksometru_do'] : null;
$gaz = intval($_POST['gaz']);
$homologacja = $_POST['homologacja_lpg_do'] !== '' ? $_POST['homologacja_lpg_do'] : null;
$firma = $_POST['firma'] !== '' ? $_POST['firma'] : null;
$forma_wlasnosci = $_POST['forma_wlasnosci'] !== '' ? $_POST['forma_wlasnosci'] : null;
$numer_polisy = $_POST['numer_polisy'] !== '' ? $_POST['numer_polisy'] : null;
$wymiana_oleju_data = isset($_POST['wymiana_oleju_data']) && $_POST['wymiana_oleju_data'] !== '' ? $_POST['wymiana_oleju_data'] : null;
$wymiana_oleju_przebieg = isset($_POST['wymiana_oleju_przebieg']) && $_POST['wymiana_oleju_przebieg'] !== '' ? intval($_POST['wymiana_oleju_przebieg']) : null;

try {
    $stmt = $pdo->prepare("INSERT INTO pojazdy (rejestracja, marka, model, przebieg, ubezpieczenie_do, przeglad_do, aktywny, inpost, taxi, taksometr, legalizacja_taksometru_do, gaz, homologacja_lpg_do, firma, forma_wlasnosci, numer_polisy, wymiana_oleju_data, wymiana_oleju_przebieg, oil_reminder_sent_at, oil_reminder_sent_type)
                           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, NULL)");
    $stmt->execute([
        $rejestracja, $marka, $model, $przebieg, $ubezpieczenie, $przeglad, $aktywny,
        $inpost, $taxi, $taksometr, $legalizacja, $gaz, $homologacja, $firma,
        $forma_wlasnosci, $numer_polisy, $wymiana_oleju_data, $wymiana_oleju_przebieg
    ]);

    echo json_encode(["status" => "success", "message" => "Pojazd dodany pomyślnie"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych: " . $e->getMessage()]);
}
?>
