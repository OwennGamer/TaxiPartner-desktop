<?php
header("Content-Type: application/json");
require_once "db.php";

try {
    $stmt = $pdo->query("SELECT id, rejestracja, marka, model, przebieg, ubezpieczenie_do, przeglad_do, aktywny, inpost, taxi, taksometr, legalizacja_taksometru_do, gaz, homologacja_lpg_do, firma, forma_wlasnosci, numer_polisy FROM pojazdy");
    $vehicles = $stmt->fetchAll(PDO::FETCH_ASSOC);
        foreach ($vehicles as &$v) {
        $v['aktywny'] = (int)$v['aktywny'] === 1;
        $v['inpost'] = (int)$v['inpost'] === 1;
        $v['taxi'] = (int)$v['taxi'] === 1;
        $v['taksometr'] = (int)$v['taksometr'] === 1;
        $v['gaz'] = (int)$v['gaz'] === 1;
    }
    unset($v);
    echo json_encode(["status" => "success", "data" => $vehicles]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd bazy danych"]);
}
?>
