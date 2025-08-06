<?php
header("Content-Type: application/json");

require_once "db.php"; // Połączenie z bazą
require_once "auth.php"; // Autoryzacja

// 🔵 Start debugowania
file_put_contents("debug_log.txt", "🔵 Skrypt addRide.php startuje\n", FILE_APPEND);

// Sprawdzenie danych wejściowych
if (!isset($_POST['driver_id'], $_POST['amount'], $_POST['type'], $_POST['source'])) {
    echo json_encode(["status" => "error", "message" => "Brak danych wejściowych"]);
    exit;
}

$driver_id = $_POST['driver_id'];
$amount = (float)$_POST['amount'];
$type = $_POST['type'];
$source = $_POST['source'];
$via_km = isset($_POST['via_km']) ? (int)$_POST['via_km'] : 0;

try {
    $pdo->beginTransaction();

    // Pobranie danych kierowcy
    $stmt = $pdo->prepare("SELECT saldo FROM kierowcy WHERE id = ?");
    $stmt->execute([$driver_id]);
    $driver = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$driver) {
        echo json_encode(["status" => "error", "message" => "Nie znaleziono kierowcy"]);
        exit;
    }

    // Pobranie warunków współpracy
    $stmtTerms = $pdo->prepare("SELECT term_name, term_value FROM collaboration_terms WHERE driver_id = ?");
    $stmtTerms->execute([$driver_id]);
    $terms = $stmtTerms->fetchAll(PDO::FETCH_KEY_PAIR);

    $percentTurnover = isset($terms['percentTurnover']) ? (float)$terms['percentTurnover'] : 0;
    $cardCommission = isset($terms['cardCommission']) ? (float)$terms['cardCommission'] : 0;
    $partnerCommission = isset($terms['partnerCommission']) ? (float)$terms['partnerCommission'] : 0;
    $boltCommission = isset($terms['boltCommission']) ? (float)$terms['boltCommission'] : 0;

    $final_amount = 0;

    // 🔥 Obliczenia według wariantów:
    if ($source === "Postój") {
        if ($type === "Karta") {
            // 1. Postój - Karta
            $after_card = $amount - ($amount * ($cardCommission / 100));
            $final_amount = $after_card * ($percentTurnover / 100);
        } elseif ($type === "Gotówka") {
            // 2. Postój - Gotówka
            $final_amount = -($amount * (1 - ($percentTurnover / 100)));
        } elseif ($type === "Voucher") {
            // 3. Postój - Voucher
            $after_partner = $amount - ($amount * ($partnerCommission / 100));
            $final_amount = $after_partner * ($percentTurnover / 100);
        }
    } elseif ($source === "Dyspozytornia") {
        if ($type === "Karta") {
            // 4. Dyspozytornia - Karta
            $after_commissions = $amount - ($amount * ($cardCommission / 100)) - ($amount * ($partnerCommission / 100));
            $final_amount = $after_commissions * ($percentTurnover / 100);
        } elseif ($type === "Gotówka") {
            // 5. Dyspozytornia - Gotówka
            $after_partner = $amount - ($amount * ($partnerCommission / 100));
            $final_amount = -( ($amount * ($partnerCommission / 100)) + ($after_partner * (1 - ($percentTurnover / 100))) );
        } elseif ($type === "Voucher") {
            // 6. Dyspozytornia - Voucher
            $after_partner = $amount - ($amount * ($partnerCommission / 100));
            $final_amount = $after_partner * ($percentTurnover / 100);
        }
    } elseif ($source === "Bolt") {
        if ($type === "Karta") {
            // 7. Bolt - Karta
            $after_bolt = $amount - ($amount * ($boltCommission / 100));
            $final_amount = $after_bolt * ($percentTurnover / 100);
        } elseif ($type === "Gotówka") {
            // 8. Bolt - Gotówka
            $after_bolt = $amount - ($amount * ($boltCommission / 100));
            $final_amount = -( ($amount * ($boltCommission / 100)) + ($after_bolt * (1 - ($percentTurnover / 100))) );
        } elseif ($type === "Voucher") {
            // 9. Bolt - Voucher
            $after_bolt = $amount - ($amount * ($boltCommission / 100));
            $final_amount = $after_bolt * ($percentTurnover / 100);
        }
    }

    // Zaokrąglij do dwóch miejsc
    $final_amount = round($final_amount, 2);

    // Nowe saldo
    $currentSaldo = (float)$driver['saldo'];
    $newSaldo = round($currentSaldo + $final_amount, 2);

    // Obsługa zdjęcia paragonu (opcjonalnie)
    $receiptPath = null;
    if (!empty($_FILES['receipt']) && $_FILES['receipt']['error'] !== UPLOAD_ERR_NO_FILE) {
        if ($_FILES['receipt']['error'] === UPLOAD_ERR_OK) {
            $uploadDir = __DIR__ . '/uploads/receipts/';
            if (!is_dir($uploadDir)) {
                if (!mkdir($uploadDir, 0777, true)) {
                    $mkdirError = error_get_last();
                    file_put_contents("debug_log.txt", "\xE2\x9D\x8C Nie mo\xC5\xBCna utworzy\xC4\x87 katalogu $uploadDir: " . ($mkdirError['message'] ?? 'unknown') . "\n", FILE_APPEND);
                    throw new Exception('Nie mo\xC5\xBCna utworzy\xC4\x87 katalogu na paragony');
                }
            }
            if (!is_writable($uploadDir)) {
                file_put_contents("debug_log.txt", "\xE2\x9D\x8C Katalog $uploadDir nie jest zapisywalny\n", FILE_APPEND);
                throw new Exception('Katalog na paragony nie jest zapisywalny');
            }
            $extension = pathinfo($_FILES['receipt']['name'], PATHINFO_EXTENSION) ?: 'jpg';
            $filename = uniqid('receipt_') . '.' . $extension;
            $destination = $uploadDir . $filename;
            if (move_uploaded_file($_FILES['receipt']['tmp_name'], $destination)) {
                $receiptPath = 'uploads/receipts/' . $filename;
            } else {
                $moveError = error_get_last();
                file_put_contents("debug_log.txt", "\xE2\x9D\x8C move_uploaded_file error: " . print_r($moveError, true) . "\n", FILE_APPEND);
                $message = 'Nie uda\xC5\x82o si\xC4\x99 przes\xC5\x82a\xC4\x87 paragonu';
                if (!empty($moveError['message'])) {
                    $message .= ': ' . $moveError['message'];
                }
                throw new Exception($message);
            }
        } else {
            throw new Exception('B\xC5\x82\xC4\x85d przesy\xC5\x82ania paragonu');
        }
    }


    // Zapisz kurs
        $stmt = $pdo->prepare("INSERT INTO kursy (driver_id, amount, saldo_wplyw, saldo_po, type, source, via_km, receipt_photo, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())");
    $stmt->execute([$driver_id, $amount, $final_amount, $newSaldo, $type, $source, $via_km, $receiptPath]);

    // Aktualizuj saldo kierowcy
    $stmt = $pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?");
    $stmt->execute([$newSaldo, $driver_id]);

    $pdo->commit();

    echo json_encode(["status" => "success", "message" => "Kurs zapisany. Zmiana salda: $final_amount"]);
    exit;

} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(["status" => "error", "message" => "Błąd: " . $e->getMessage()]);
    exit;
}
?>

