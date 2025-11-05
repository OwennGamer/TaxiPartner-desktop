<?php
declare(strict_types=1);

require_once __DIR__ . '/config.php';
require_once __DIR__ . '/db.php';
require_once __DIR__ . '/fcm_v1.php';

// Ensure consistent timezone (database usually uses UTC, but logs should match local expectations)
@date_default_timezone_set('Europe/Warsaw');

$logFile = __DIR__ . '/debug_fixed_costs.log';
$today   = new DateTimeImmutable('now');

// Safety guard – run logic only on the first day of the month
if ((int)$today->format('j') !== 1) {
    file_put_contents($logFile, sprintf("[%s] Skipped – not the first day of the month.%s", $today->format(DateTimeInterface::ATOM), PHP_EOL), FILE_APPEND);
    exit(0);
}

$monthKey      = $today->format('Y-m');
$humanReadable = $today->format('Y-m');

$driversStmt = $pdo->prepare(
    "SELECT k.id, k.saldo, k.fcm_token, COALESCE(ct.term_value, 0) AS fixed_costs
       FROM kierowcy k
  LEFT JOIN collaboration_terms ct
         ON ct.driver_id = k.id AND ct.term_name = 'fixedCosts'
      WHERE COALESCE(ct.term_value, 0) <> 0"
);
$driversStmt->execute();
$drivers = $driversStmt->fetchAll(PDO::FETCH_ASSOC);

if (!$drivers) {
    file_put_contents($logFile, sprintf("[%s] Brak kierowców z kosztami stałymi.%s", $today->format(DateTimeInterface::ATOM), PHP_EOL), FILE_APPEND);
    exit(0);
}

$historyCheckStmt = $pdo->prepare(
    "SELECT COUNT(*) FROM historia_salda WHERE kierowca_id = ? AND counter_type = 'fixed_costs' AND DATE_FORMAT(data, '%Y-%m') = ?"
);
$selectSaldoStmt = $pdo->prepare("SELECT saldo FROM kierowcy WHERE id = ? FOR UPDATE");
$updateSaldoStmt = $pdo->prepare("UPDATE kierowcy SET saldo = ? WHERE id = ?");
$insertHistoryStmt = $pdo->prepare(
    "INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod, counter_type) VALUES (?, ?, ?, ?, 'fixed_costs')"
);

$processed = 0;
$errors = 0;
$fcmSent = 0;

foreach ($drivers as $driver) {
    $driverId    = $driver['id'];
    $fixedCosts  = (float)$driver['fixed_costs'];

    if (abs($fixedCosts) < 1e-6) {
        continue;
    }

    try {
        $pdo->beginTransaction();

        // Avoid double-charging within the same month
        $historyCheckStmt->execute([$driverId, $monthKey]);
        if ((int)$historyCheckStmt->fetchColumn() > 0) {
            $pdo->commit();
            continue;
        }

        $selectSaldoStmt->execute([$driverId]);
        $currentSaldoRow = $selectSaldoStmt->fetch(PDO::FETCH_ASSOC);
        if (!$currentSaldoRow) {
            $pdo->rollBack();
            file_put_contents($logFile, sprintf("[%s] Kierowca %s nie znaleziony podczas naliczania kosztów.%s", $today->format(DateTimeInterface::ATOM), $driverId, PHP_EOL), FILE_APPEND);
            $errors++;
            continue;
        }

        $currentSaldo = (float)$currentSaldoRow['saldo'];
        $change       = -abs($fixedCosts);
        $newSaldo     = round($currentSaldo + $change, 2);

        $updateSaldoStmt->execute([$newSaldo, $driverId]);

        $reason = sprintf('Koszty stałe za %s', $humanReadable);
        $insertHistoryStmt->execute([$driverId, $change, $newSaldo, $reason]);

        $pdo->commit();
        $processed++;

        $fcmToken = $driver['fcm_token'] ?? null;
        if ($fcmToken) {
            $title = 'Koszty stałe';
            $body  = sprintf('Pobrano %s zł. Saldo po: %s zł.',
                number_format(abs($change), 2, ',', ' '),
                number_format($newSaldo, 2, ',', ' ')
            );
            $dataPayload = [
                'type'        => 'fixed_costs',
                'driver_id'   => (string)$driverId,
                'amount'      => sprintf('%.2f', abs($change)),
                'saldo_after' => sprintf('%.2f', $newSaldo),
                'period'      => $humanReadable,
            ];
            $resp = sendFcmV1($fcmToken, $title, $body, $dataPayload);
            if ($resp !== null) {
                $fcmSent++;
                file_put_contents($logFile, sprintf("[%s] Wysłano FCM do %s: %s%s", $today->format(DateTimeInterface::ATOM), $driverId, json_encode($resp, JSON_UNESCAPED_UNICODE), PHP_EOL), FILE_APPEND);
            } else {
                file_put_contents($logFile, sprintf("[%s] Pominieto FCM dla %s (brak konfiguracji).%s", $today->format(DateTimeInterface::ATOM), $driverId, PHP_EOL), FILE_APPEND);
            }
        } else {
            file_put_contents($logFile, sprintf("[%s] Brak tokenu FCM dla kierowcy %s.%s", $today->format(DateTimeInterface::ATOM), $driverId, PHP_EOL), FILE_APPEND);
        }
    } catch (Throwable $e) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        $errors++;
        file_put_contents($logFile, sprintf("[%s] Błąd naliczania kosztów dla %s: %s%s", $today->format(DateTimeInterface::ATOM), $driverId, $e->getMessage(), PHP_EOL), FILE_APPEND);
    }
}

file_put_contents(
    $logFile,
    sprintf(
        "[%s] Podsumowanie: przetworzono=%d, bledy=%d, fcm=%d%s",
        $today->format(DateTimeInterface::ATOM),
        $processed,
        $errors,
        $fcmSent,
        PHP_EOL
    ),
    FILE_APPEND
);

exit($errors > 0 ? 1 : 0);
