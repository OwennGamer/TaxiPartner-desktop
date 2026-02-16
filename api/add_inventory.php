<?php
// add_inventory.php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';       // getAuthorizationHeader() i verifyJWT()
require_once __DIR__ . '/jwt_utils.php';
require_once __DIR__ . '/mailer_utils.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// debug
file_put_contents(__DIR__ . "/debug_inventory.txt",
    "[" . date('Y-m-d H:i:s') . "] add_inventory.php called\n", FILE_APPEND);
file_put_contents(__DIR__ . "/debug_inventory.txt",
    "POST: " . json_encode($_POST) . "\n", FILE_APPEND);
file_put_contents(__DIR__ . "/debug_inventory.txt",
    "FILES: " . json_encode($_FILES) . "\n", FILE_APPEND);

// metoda musi być POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

// AUTORYZACJA: pobranie i weryfikacja JWT z nagłówka Authorization
$token = getAuthorizationHeader();
if (!$token) {
    http_response_code(401);
    echo json_encode(['status'=>'error','message'=>'Brak tokena']);
    exit;
}
$decoded = verifyJWT($token);
if (!$decoded) {
    http_response_code(401);
    echo json_encode(['status'=>'error','message'=>'Nieprawidłowy token']);
    exit;
}

// ID kierowcy z tokena (alfanumeryczne)
$kierowca_id = $decoded->user_id;
file_put_contents(__DIR__ . "/debug_inventory.txt",
    "KIEROWCA_ID = {$kierowca_id}\n", FILE_APPEND);

// Required fields
$rejestracja      = trim($_POST['rejestracja']        ?? '');
$przebieg         = (int)($_POST['przebieg']       ?? 0);
$czyste_wewnatrz  = isset($_POST['czyste_wewnatrz']) ? (int)$_POST['czyste_wewnatrz'] : 1;
$kamizelki_qty    = isset($_POST['kamizelki_qty'])   ? (int)$_POST['kamizelki_qty']   : null;

// Equipment flags
$karta_paliwowa_e100 = isset($_POST['karta_paliwowa_e100']) ? (int)$_POST['karta_paliwowa_e100'] : 0;
$magnesy_partner     = isset($_POST['magnesy_partner'])     ? (int)$_POST['magnesy_partner']     : 0;
$numery_boczne       = isset($_POST['numery_boczne'])       ? (int)$_POST['numery_boczne']       : 0;
$wizytowki           = isset($_POST['wizytowki'])           ? (int)$_POST['wizytowki']           : 0;
$terminal_platniczy  = isset($_POST['terminal_platniczy'])  ? (int)$_POST['terminal_platniczy']  : 0;
$ladowarka_terminala = isset($_POST['ladowarka_terminala']) ? (int)$_POST['ladowarka_terminala'] : 0;
$ladowarka           = isset($_POST['ladowarka'])           ? (int)$_POST['ladowarka']           : 0;
$kabel_usb           = isset($_POST['kabel_usb'])           ? (int)$_POST['kabel_usb']           : 0;
$uchwyt_telefon      = isset($_POST['uchwyt_telefon'])      ? (int)$_POST['uchwyt_telefon']      : 0;
$lampa_taxi          = isset($_POST['lampa_taxi'])          ? (int)$_POST['lampa_taxi']          : 0;
$licencja            = isset($_POST['licencja'])            ? (int)$_POST['licencja']            : 0;
$legalizacja      = isset($_POST['legalizacja'])      ? (int)$_POST['legalizacja']      : 0;
$dowod            = isset($_POST['dowod'])            ? (int)$_POST['dowod']            : 0;
$ubezpieczenie    = isset($_POST['ubezpieczenie'])    ? (int)$_POST['ubezpieczenie']    : 0;
$karta_lotniskowa = isset($_POST['karta_lotniskowa']) ? (int)$_POST['karta_lotniskowa'] : 0;
$gasnica          = isset($_POST['gasnica'])          ? (int)$_POST['gasnica']          : 0;
$lewarek          = isset($_POST['lewarek'])          ? (int)$_POST['lewarek']          : 0;
$trojkat          = isset($_POST['trojkat'])          ? (int)$_POST['trojkat']          : 0;
$kamizelka        = isset($_POST['kamizelka'])        ? (int)$_POST['kamizelka']        : 0;

// Optional notes
$uwagi            = trim($_POST['uwagi'] ?? '') !== '' ? $_POST['uwagi'] : null;

// Basic validation
if ($rejestracja === '') {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Brakuje rejestracji']);
    exit;
}
if ($przebieg <= 0) {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Nieprawidłowy przebieg']);
    exit;
}

// Upload directory
$uploadDir = __DIR__ . '/uploads/inventory/';
if (!is_dir($uploadDir)) mkdir($uploadDir, 0777, true);

function uploadPhoto(string $field, string $prefix, string $uploadDir): ?string {
    if (empty($_FILES[$field]) || $_FILES[$field]['error'] !== UPLOAD_ERR_OK) {
        return null;
    }
    $filename = uniqid($prefix) . '.jpg';
    $target   = $uploadDir . $filename;
    if (move_uploaded_file($_FILES[$field]['tmp_name'], $target)) {
        return 'uploads/inventory/' . $filename;
    }
    return null;
}

function qtyLabel($value): string {
    return $value === null || $value === '' ? 'brak danych' : (string)$value;
}

function escapeHtml(string $value): string {
    return htmlspecialchars($value, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}

// Zdjęcia pojazdu
$photoFront = uploadPhoto('photo_front','front_',$uploadDir);
$photoBack  = uploadPhoto('photo_back', 'back_',$uploadDir);
$photoLeft  = uploadPhoto('photo_left', 'left_',$uploadDir);
$photoRight = uploadPhoto('photo_right','right_',$uploadDir);

// Zdjęcia zabrudzeń
$photoDirt1 = uploadPhoto('photo_dirt1','dirt1_',$uploadDir);
$photoDirt2 = uploadPhoto('photo_dirt2','dirt2_',$uploadDir);
$photoDirt3 = uploadPhoto('photo_dirt3','dirt3_',$uploadDir);
$photoDirt4 = uploadPhoto('photo_dirt4','dirt4_',$uploadDir);

// Walidacja zabrudzeń
if ($czyste_wewnatrz === 0 &&
    empty($photoDirt1) &&
    empty($photoDirt2) &&
    empty($photoDirt3) &&
    empty($photoDirt4)
) {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Potrzebne zdjęcie zabrudzenia']);
    exit;
}

try {
    // Pobranie poprzedniej inwentaryzacji dla porównania
    $prevStmt = $pdo->prepare("
        SELECT id, czyste_wewnatrz, karta_paliwowa_e100, magnesy_partner, numery_boczne,
               wizytowki, terminal_platniczy, ladowarka_terminala, ladowarka, kabel_usb,
               uchwyt_telefon, lampa_taxi, licencja, dowod, ubezpieczenie,
               karta_lotniskowa, gasnica, lewarek, trojkat,
               kamizelki_qty, uwagi, kierowca_id
          FROM inwentaryzacje
         WHERE LOWER(rejestracja) = LOWER(:re)
         ORDER BY data_dodania DESC, id DESC
         LIMIT 1
    ");
    $prevStmt->execute([':re' => $rejestracja]);
    $prevInventory = $prevStmt->fetch(PDO::FETCH_ASSOC) ?: null;

    $differences = [];
    $differenceRows = [];
    $differenceNotes = [];

    if ($czyste_wewnatrz === 0) {
        $prevClean = $prevInventory ? (int)($prevInventory['czyste_wewnatrz'] ?? 0) : null;
        $msg = 'Auto czyste wewnątrz? Nie';
        if ($prevClean === 1) {
            $msg .= ' (poprzednio Tak)';
        }
        $differences[] = $msg;
        $differenceRows[] = [
            'label' => 'Czyste wewnątrz',
            'type'  => 'alert',
            'value' => $prevClean === 1 ? 'Nie (poprzednio: Tak)' : 'Nie'
        ];
    }

    if ($prevInventory) {
        $toggleMessages = [
            'karta_paliwowa_e100' => [
                'on_to_off' => 'Poprzednio karta paliwowa E100 była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio karta paliwowa E100 nie była zaznaczona a teraz jest',
            ],
            'magnesy_partner' => [
                'on_to_off' => 'Poprzednio magnesy Partner były zaznaczone a teraz nie są',
                'off_to_on' => 'Poprzednio magnesy Partner nie były zaznaczone a teraz są',
            ],
            'numery_boczne' => [
                'on_to_off' => 'Poprzednio numery boczne były zaznaczone a teraz nie są',
                'off_to_on' => 'Poprzednio numery boczne nie były zaznaczone a teraz są',
            ],
            'wizytowki' => [
                'on_to_off' => 'Poprzednio wizytówki były zaznaczone a teraz nie są',
                'off_to_on' => 'Poprzednio wizytówki nie były zaznaczone a teraz są',
            ],
            'terminal_platniczy' => [
                'on_to_off' => 'Poprzednio terminal płatniczy był zaznaczony a teraz nie jest',
                'off_to_on' => 'Poprzednio terminal płatniczy nie był zaznaczony a teraz jest',
            ],
            'ladowarka_terminala' => [
                'on_to_off' => 'Poprzednio ładowarka do terminala była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio ładowarka do terminala nie była zaznaczona a teraz jest',
            ],
            'ladowarka' => [
                'on_to_off' => 'Poprzednio ładowarka była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio ładowarka nie była zaznaczona a teraz jest',
            ],
            'kabel_usb' => [
                'on_to_off' => 'Poprzednio kabel USB był zaznaczony a teraz nie jest',
                'off_to_on' => 'Poprzednio kabel USB nie był zaznaczony a teraz jest',
            ],
            'uchwyt_telefon' => [
                'on_to_off' => 'Poprzednio uchwyt na telefon był zaznaczony a teraz nie jest',
                'off_to_on' => 'Poprzednio uchwyt na telefon nie był zaznaczony a teraz jest',
            ],
            'lampa_taxi' => [
                'on_to_off' => 'Poprzednio lampa TAXI była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio lampa TAXI nie była zaznaczona a teraz jest',
            ],
            'licencja' => [
                'label'     => 'Licencja',
                'on_to_off' => 'Poprzednio licencja była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio licencja nie była zaznaczona a teraz jest',
            ],
            'dowod' => [
                'label'     => 'Kopia dowodu',
                'on_to_off' => 'Poprzednio kopia dowodu była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio kopia dowodu nie była zaznaczona a teraz jest',
            ],
            'ubezpieczenie' => [
                'label'     => 'Kopia ubezpieczenia',
                'on_to_off' => 'Poprzednio kopia ubezpieczenia była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio kopia ubezpieczenia nie była zaznaczona a teraz jest',
            ],
            'karta_lotniskowa' => [
                'label'     => 'Karta lotniskowa',
                'on_to_off' => 'Poprzednio karta lotniskowa była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio karta lotniskowa nie była zaznaczona a teraz jest',
            ],
            'gasnica' => [
                'label'     => 'Gaśnica',
                'on_to_off' => 'Poprzednio gaśnica była zaznaczona a teraz nie jest',
                'off_to_on' => 'Poprzednio gaśnica nie była zaznaczona a teraz jest',
            ],
            'lewarek' => [
                'label'     => 'Lewarek',
                'on_to_off' => 'Poprzednio lewarek był zaznaczony a teraz nie jest',
                'off_to_on' => 'Poprzednio lewarek nie był zaznaczony a teraz jest',
            ],
            'trojkat' => [
                'label'     => 'Trójkąt',
                'on_to_off' => 'Poprzednio trójkąt był zaznaczony a teraz nie jest',
                'off_to_on' => 'Poprzednio trójkąt nie był zaznaczony a teraz jest',
            ],
        ];

        foreach ($toggleMessages as $field => $messages) {
            $prevValue = (int)($prevInventory[$field] ?? 0);
            $currentValue = (int)(${$field} ?? 0);

            if ($prevValue === 1 && $currentValue === 0) {
                $differences[] = $messages['on_to_off'];
                $differenceRows[] = [
                    'label' => $messages['label'],
                    'type'  => 'removed',
                    'value' => 'Brak (poprzednio: Tak)'
                ];
            } elseif ($prevValue === 0 && $currentValue === 1) {
                $differences[] = $messages['off_to_on'];
                $differenceRows[] = [
                    'label' => $messages['label'],
                    'type'  => 'added',
                    'value' => 'Dodano (poprzednio: Nie)'
                ];
            }
        }

        $prevQty = $prevInventory['kamizelki_qty'] ?? null;
        if ($prevQty !== null) {
            $prevQty = (int)$prevQty;
        }
        if ($prevQty !== $kamizelki_qty) {
            $differences[] = 'Ilość podanych kamizelek różni się od poprzednio zapisanej ilości (poprzednio '
                . qtyLabel($prevQty) . ', teraz ' . qtyLabel($kamizelki_qty) . ')';
            $differenceRows[] = [
                'label' => 'Kamizelki',
                'type'  => 'quantity',
                'value' => 'Poprzednio: ' . qtyLabel($prevQty) . ', Teraz: ' . qtyLabel($kamizelki_qty)
            ];
        }
    }

    if ($uwagi !== null && trim($uwagi) !== '') {
        $differences[] = 'Czy masz dodatkowe uwagi - Tak - ' . $uwagi;
        $differenceNotes[] = $uwagi;
    }

    // 1) Wstawiamy rekord inwentaryzacji
    $sql = "INSERT INTO inwentaryzacje
      (rejestracja, przebieg, czyste_wewnatrz,
       photo_front, photo_back, photo_left, photo_right,
       photo_dirt1, photo_dirt2, photo_dirt3, photo_dirt4,
       karta_paliwowa_e100, magnesy_partner, numery_boczne, wizytowki,
       terminal_platniczy, ladowarka_terminala, ladowarka, kabel_usb, uchwyt_telefon, lampa_taxi,
       licencja, legalizacja, dowod, ubezpieczenie,
       karta_lotniskowa, gasnica, lewarek, trojkat,
       kamizelka, kamizelki_qty, uwagi, kierowca_id)
    VALUES
      (:re, :prz, :cz,
       :pf, :pb, :pl, :prr,
       :pd1, :pd2, :pd3, :pd4,
       :e100, :mag, :num, :wiz, :ter, :lter, :lad, :usb, :uch, :lamp,
       :lic, :leg, :dow, :ube,
       :kart, :gas, :lew, :tro,
       :kam, :kq, :uw, :kid)";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':re'   => $rejestracja,
        ':prz'  => $przebieg,
        ':cz'   => $czyste_wewnatrz,
        ':pf'   => $photoFront,
        ':pb'   => $photoBack,
        ':pl'   => $photoLeft,
        ':prr'  => $photoRight,
        ':pd1'  => $photoDirt1,
        ':pd2'  => $photoDirt2,
        ':pd3'  => $photoDirt3,
        ':pd4'  => $photoDirt4,
	':e100' => $karta_paliwowa_e100,
        ':mag'  => $magnesy_partner,
        ':num'  => $numery_boczne,
        ':wiz'  => $wizytowki,
        ':ter'  => $terminal_platniczy,
        ':lter' => $ladowarka_terminala,
        ':lad'  => $ladowarka,
        ':usb'  => $kabel_usb,
        ':uch'  => $uchwyt_telefon,
        ':lamp' => $lampa_taxi,
        ':lic'  => $licencja,
        ':leg'  => $legalizacja,
        ':dow'  => $dowod,
        ':ube'  => $ubezpieczenie,
        ':kart' => $karta_lotniskowa,
        ':gas'  => $gasnica,
        ':lew'  => $lewarek,
        ':tro'  => $trojkat,
        ':kam'  => $kamizelka,
        ':kq'   => $kamizelki_qty,
        ':uw'   => $uwagi,
        ':kid'  => $kierowca_id
    ]);

    // 2) I od razu aktualizujemy ostatniego kierowcę w tabeli pojazdy
    $update = $pdo->prepare("
      UPDATE pojazdy
        SET ostatni_kierowca_id = :kid
      WHERE LOWER(rejestracja) = LOWER(:re)
    ");
    $update->execute([
      ':kid' => $kierowca_id,
      ':re'  => $rejestracja
    ]);

    // 3) Aktualizacja nowej kolumny last_vehicle_plate w tabeli kierowcy
    $upd2 = $pdo->prepare("
      UPDATE kierowcy
         SET last_vehicle_plate = :re
       WHERE id = :kid
    ");
    $upd2->execute([
      ':re'  => $rejestracja,
      ':kid' => $kierowca_id
    ]);

    if (!empty($differences)) {
        if (loadMailer()) {
            try {
                $mail = new PHPMailer(true);
                $mail->CharSet = 'UTF-8';
                $mail->Encoding = 'base64';

                $mail->isSMTP();
                $mail->Host = 'smtp.gmail.com';
                $mail->SMTPAuth = true;
                $mail->Username = 'aplikacja.partnertaxi@gmail.com';
                $mail->Password = 'scfj ojvw fejw oewh';
                $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
                $mail->Port = 587;

                $mail->setFrom('aplikacja.partnertaxi@gmail.com', 'Partner Taxi System');
                $mail->addAddress('serwis@taxi-partner.com.pl');

                $mail->isHTML(true);
                $subjectPlate = preg_replace("/[\r\n]+/", ' ', $rejestracja);
                $mail->Subject = 'WYKRYTO RÓŻNICĘ W STOSUNKU DO POPRZEDNIEJ INWENTARYZACJI "' . $subjectPlate . '"';

                $body = '<p>Wykryto różnice w stosunku do poprzedniej inwentaryzacji pojazdu <strong>'
                    . escapeHtml($rejestracja) . '</strong>.</p>';
                $body .= '<p>Kierowca: <strong>' . escapeHtml($kierowca_id) . '</strong></p>';

                $prevDriverId = $prevInventory['kierowca_id'] ?? null;
                $body .= '<p>Poprzednią inwentaryzację przeprowadził: <strong>'
                    . escapeHtml($prevDriverId ?: 'brak danych') . '</strong></p>';

                if (!empty($differenceRows)) {
                    $body .= '<table style="border-collapse: collapse; width: 100%; max-width: 700px; margin-top: 6px;">';
                    $body .= '<thead><tr>'
                        . '<th style="text-align: left; border-bottom: 1px solid #ccc; padding: 8px;">Pozycja</th>'
                        . '<th style="text-align: left; border-bottom: 1px solid #ccc; padding: 8px;">Zmiana</th>'
                        . '</tr></thead><tbody>';

                    foreach ($differenceRows as $row) {
                        $style = 'padding: 8px; border-bottom: 1px solid #eee;';
                        if ($row['type'] === 'removed') {
                            $style .= ' background-color: #f8d7da; color: #842029;';
                        } elseif ($row['type'] === 'added') {
                            $style .= ' background-color: #d1e7dd; color: #0f5132;';
                        } elseif ($row['type'] === 'alert') {
                            $style .= ' background-color: #fff3cd; color: #664d03;';
                        }

                        $body .= '<tr>'
                            . '<td style="padding: 8px; border-bottom: 1px solid #eee; font-weight: 600;">' . escapeHtml($row['label']) . '</td>'
                            . '<td style="' . $style . '">' . escapeHtml($row['value']) . '</td>'
                            . '</tr>';
                    }

                    $body .= '</tbody></table>';
                }

                if (!empty($differenceNotes)) {
                    $body .= '<p><strong>Dodatkowe uwagi:</strong></p><ul>';
                    foreach ($differenceNotes as $note) {
                        $body .= '<li>' . escapeHtml($note) . '</li>';
                    }
                    $body .= '</ul>';
                }

                $mail->Body = $body;
                $mail->send();
            } catch (Exception $e) {
                error_log('Błąd przy wysyłaniu maila (inwentaryzacja): ' . $e->getMessage());
            }
        } else {
            error_log('PHPMailer unavailable – pominięto wysłanie powiadomienia o różnicach inwentaryzacji.');
        }
    }

    // 4) Zwracamy sukces
    echo json_encode([
        'status'  => 'success',
        'message' => 'Inwentaryzacja zapisana',
        'id'      => $pdo->lastInsertId()
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status'=>'error','message'=>'Błąd bazy danych']);
}
