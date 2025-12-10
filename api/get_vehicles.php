<?php
header("Content-Type: application/json");
require_once "db.php";

try {
    $stmt = $pdo->query("SELECT p.id, p.rejestracja, p.marka, p.model, p.przebieg, p.ubezpieczenie_do, p.przeglad_do, p.aktywny, p.inpost, p.taxi, p.taksometr, p.legalizacja_taksometru_do, p.gaz, p.homologacja_lpg_do, p.firma, p.forma_wlasnosci, p.numer_polisy, (SELECT MAX(data_dodania) FROM inwentaryzacje i WHERE i.rejestracja = p.rejestracja) AS ostatnia_inwentaryzacja FROM pojazdy p");
    $vehicles = $stmt->fetchAll(PDO::FETCH_ASSOC);
        foreach ($vehicles as &$v) {
        $v['aktywny'] = (bool)$v['aktywny'];
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
