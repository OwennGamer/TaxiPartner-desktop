<?php
declare(strict_types=1);

require_once __DIR__ . '/voucher_utils.php';

@date_default_timezone_set('Europe/Warsaw');

$logFile = __DIR__ . '/debug_voucher_transfer.log';

$options = getopt('', ['force', 'date::', 'driver-id::']);
$forceRun = array_key_exists('force', $options);
$driverIdFilter = isset($options['driver-id']) && is_string($options['driver-id']) ? trim($options['driver-id']) : null;

$today = new DateTimeImmutable('now');
if (isset($options['date']) && is_string($options['date']) && trim($options['date']) !== '') {
    $customDate = DateTimeImmutable::createFromFormat('Y-m-d', trim($options['date']));
    if ($customDate !== false) {
        $today = $customDate;
    }
}

if (!$forceRun && (int)$today->format('j') !== 25) {
    file_put_contents(
        $logFile,
        sprintf("[%s] Skipped – not the 25th day of the month.%s", $today->format(DateTimeInterface::ATOM), PHP_EOL),
        FILE_APPEND
    );
    exit(0);
}

require_once __DIR__ . '/db.php';

$monthKey = $today->format('Y-m');
$reason = sprintf('Automatyczne przeksięgowanie Voucher (poprzedni) do salda za %s', $monthKey);

$driversSql = "SELECT id, voucher_current_amount, voucher_current_month, voucher_previous_amount, voucher_previous_month
               FROM kierowcy";
$params = [];
if ($driverIdFilter !== null && $driverIdFilter !== '') {
    $driversSql .= " WHERE id = ?";
    $params[] = $driverIdFilter;
}
$driversStmt = $pdo->prepare($driversSql);
$driversStmt->execute($params);
$drivers = $driversStmt->fetchAll(PDO::FETCH_ASSOC);

if (!$drivers) {
    file_put_contents(
        $logFile,
        sprintf("[%s] Brak kierowców do przeksięgowania.%s", $today->format(DateTimeInterface::ATOM), PHP_EOL),
        FILE_APPEND
    );
    exit(0);
}

$historyCheckStmt = $pdo->prepare(
    "SELECT COUNT(*) FROM historia_salda WHERE kierowca_id = ? AND counter_type = 'saldo' AND powod = ?"
);
$driverLockStmt = $pdo->prepare(
    "SELECT id, saldo, voucher_previous_amount FROM kierowcy WHERE id = ? FOR UPDATE"
);
$updateDriverStmt = $pdo->prepare(
    "UPDATE kierowcy SET saldo = ?, voucher_previous_amount = 0 WHERE id = ?"
);
$insertHistoryStmt = $pdo->prepare(
    "INSERT INTO historia_salda (kierowca_id, zmiana, saldo_po, powod, counter_type) VALUES (?, ?, ?, ?, ?)"
);

$processed = 0;
$skipped = 0;
$errors = 0;

foreach ($drivers as $driver) {
    $driver = voucher_refresh_buckets($pdo, $driver);
    $driverId = $driver['id'];

    if (abs((float)($driver['voucher_previous_amount'] ?? 0)) < 0.01) {
        $skipped++;
        continue;
    }

    try {
        $pdo->beginTransaction();

        $historyCheckStmt->execute([$driverId, $reason]);
        if ((int)$historyCheckStmt->fetchColumn() > 0) {
            $pdo->commit();
            $skipped++;
            continue;
        }

        $driverLockStmt->execute([$driverId]);
        $lockedDriver = $driverLockStmt->fetch(PDO::FETCH_ASSOC);

        if (!$lockedDriver) {
            $pdo->rollBack();
            $errors++;
            file_put_contents(
                $logFile,
                sprintf("[%s] Kierowca %s nie znaleziony podczas przeksięgowania.%s", $today->format(DateTimeInterface::ATOM), $driverId, PHP_EOL),
                FILE_APPEND
            );
            continue;
        }

        $voucherPreviousAmount = round((float)$lockedDriver['voucher_previous_amount'], 2);
        if (abs($voucherPreviousAmount) < 0.01) {
            $pdo->commit();
            $skipped++;
            continue;
        }

        $newSaldo = round((float)$lockedDriver['saldo'] + $voucherPreviousAmount, 2);

        $updateDriverStmt->execute([$newSaldo, $driverId]);

        $insertHistoryStmt->execute([$driverId, $voucherPreviousAmount, $newSaldo, $reason, 'saldo']);
        $insertHistoryStmt->execute([$driverId, -$voucherPreviousAmount, 0.00, $reason, 'voucher_previous']);

        $pdo->commit();
        $processed++;
    } catch (Throwable $e) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }

        $errors++;
        file_put_contents(
            $logFile,
            sprintf("[%s] Błąd przeksięgowania dla kierowcy %s: %s%s", $today->format(DateTimeInterface::ATOM), $driverId, $e->getMessage(), PHP_EOL),
            FILE_APPEND
        );
    }
}

file_put_contents(
    $logFile,
    sprintf(
        "[%s] Podsumowanie: przetworzono=%d, pominieto=%d, bledy=%d, force=%s, driver_filter=%s%s",
        $today->format(DateTimeInterface::ATOM),
        $processed,
        $skipped,
        $errors,
        $forceRun ? 'true' : 'false',
        $driverIdFilter !== null && $driverIdFilter !== '' ? $driverIdFilter : '-',
        PHP_EOL
    ),
    FILE_APPEND
);

exit($errors > 0 ? 1 : 0);
