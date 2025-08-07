<?php
require_once 'auth.php';
require_once 'db.php';

header('Content-Type: application/json');

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    echo json_encode(["status" => "error", "message" => "Brak ważnego tokena"]);
    exit;
}
if ($decoded->role !== 'admin') {
    echo json_encode(["status" => "error", "message" => "Brak uprawnień"]);
    exit;
}

$data = json_decode(file_get_contents('php://input'), true);

if (!isset($data['id']) || !isset($data['name'])) {
    echo json_encode(["status" => "error", "message" => "Brak wymaganych pól"]);
    exit;
}

try {
    $stmt = $pdo->prepare("UPDATE pracownicy SET
        name=:name, firma=:firma, rodzaj_umowy=:rodzaj_umowy, data_umowy=:data_umowy,
        dowod=:dowod, prawo_jazdy=:prawo_jazdy, niekaralnosc=:niekaralnosc,
        orzeczenie_psychologiczne=:orzeczenie_psychologiczne,
        data_badania_psychologicznego=:data_badania_psychologicznego,
        orzeczenie_lekarskie=:orzeczenie_lekarskie,
        data_badan_lekarskich=:data_badan_lekarskich,
        informacja_ppk=:informacja_ppk, rezygnacja_ppk=:rezygnacja_ppk,
        forma_wyplaty=:forma_wyplaty,
        wynagrodzenie_do_rak_wlasnych=:wynagrodzenie_do_rak_wlasnych,
        zgoda_na_przelew=:zgoda_na_przelew,
        ryzyko_zawodowe=:ryzyko_zawodowe,
        oswiadczenie_zus=:oswiadczenie_zus,
        bhp=:bhp,
        regulamin_pracy=:regulamin_pracy,
        zasady_ewidencji_kasa=:zasady_ewidencji_kasa,
        pit2=:pit2,
        oswiadczenie_art188_kp=:oswiadczenie_art188_kp,
        rodo=:rodo,
        pora_nocna=:pora_nocna,
        pit_email=:pit_email,
        osoba_kontaktowa=:osoba_kontaktowa,
        numer_prywatny=:numer_prywatny,
        numer_sluzbowy=:numer_sluzbowy,
        model_telefonu_sluzbowego=:model_telefonu_sluzbowego,
        operator=:operator,
        waznosc_wizy=:waznosc_wizy
        WHERE id=:id");

    $stmt->execute([
        ':id' => $data['id'],
        ':name' => $data['name'],
        ':firma' => $data['firma'] ?? null,
        ':rodzaj_umowy' => $data['rodzaj_umowy'] ?? null,
        ':data_umowy' => $data['data_umowy'] ?? null,
        ':dowod' => isset($data['dowod']) ? (int)$data['dowod'] : 0,
        ':prawo_jazdy' => isset($data['prawo_jazdy']) ? (int)$data['prawo_jazdy'] : 0,
        ':niekaralnosc' => isset($data['niekaralnosc']) ? (int)$data['niekaralnosc'] : 0,
        ':orzeczenie_psychologiczne' => isset($data['orzeczenie_psychologiczne']) ? (int)$data['orzeczenie_psychologiczne'] : 0,
        ':data_badania_psychologicznego' => $data['data_badania_psychologicznego'] ?? null,
        ':orzeczenie_lekarskie' => isset($data['orzeczenie_lekarskie']) ? (int)$data['orzeczenie_lekarskie'] : 0,
        ':data_badan_lekarskich' => $data['data_badan_lekarskich'] ?? null,
        ':informacja_ppk' => isset($data['informacja_ppk']) ? (int)$data['informacja_ppk'] : 0,
        ':rezygnacja_ppk' => isset($data['rezygnacja_ppk']) ? (int)$data['rezygnacja_ppk'] : 0,
        ':forma_wyplaty' => $data['forma_wyplaty'] ?? null,
        ':wynagrodzenie_do_rak_wlasnych' => isset($data['wynagrodzenie_do_rak_wlasnych']) ? (int)$data['wynagrodzenie_do_rak_wlasnych'] : 0,
        ':zgoda_na_przelew' => isset($data['zgoda_na_przelew']) ? (int)$data['zgoda_na_przelew'] : 0,
        ':ryzyko_zawodowe' => isset($data['ryzyko_zawodowe']) ? (int)$data['ryzyko_zawodowe'] : 0,
        ':oswiadczenie_zus' => isset($data['oswiadczenie_zus']) ? (int)$data['oswiadczenie_zus'] : 0,
        ':bhp' => isset($data['bhp']) ? (int)$data['bhp'] : 0,
        ':regulamin_pracy' => isset($data['regulamin_pracy']) ? (int)$data['regulamin_pracy'] : 0,
        ':zasady_ewidencji_kasa' => isset($data['zasady_ewidencji_kasa']) ? (int)$data['zasady_ewidencji_kasa'] : 0,
        ':pit2' => isset($data['pit2']) ? (int)$data['pit2'] : 0,
        ':oswiadczenie_art188_kp' => isset($data['oswiadczenie_art188_kp']) ? (int)$data['oswiadczenie_art188_kp'] : 0,
        ':rodo' => isset($data['rodo']) ? (int)$data['rodo'] : 0,
        ':pora_nocna' => isset($data['pora_nocna']) ? (int)$data['pora_nocna'] : 0,
        ':pit_email' => $data['pit_email'] ?? null,
        ':osoba_kontaktowa' => $data['osoba_kontaktowa'] ?? null,
        ':numer_prywatny' => $data['numer_prywatny'] ?? null,
        ':numer_sluzbowy' => $data['numer_sluzbowy'] ?? null,
        ':model_telefonu_sluzbowego' => $data['model_telefonu_sluzbowego'] ?? null,
        ':operator' => $data['operator'] ?? null,
        ':waznosc_wizy' => $data['waznosc_wizy'] ?? null
    ]);

    echo json_encode(["status" => "success"]);
} catch (PDOException $e) {
    echo json_encode(["status" => "error", "message" => "Błąd SQL: " . $e->getMessage()]);
}
?>
