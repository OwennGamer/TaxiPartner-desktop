<?php
header('Content-Type: application/json');

require_once __DIR__ . '/db.php';
require_once __DIR__ . '/voucher_utils.php';

if (PHP_SAPI !== 'cli') {
    require_once __DIR__ . '/auth.php';
    $authenticatedUser = getAuthenticatedJwt();
    $userRole = strtolower((string)($authenticatedUser->role ?? ''));
    if (!in_array($userRole, ['admin', 'administrator'], true)) {
        http_response_code(403);
        echo json_encode(['status' => 'error', 'message' => 'Brak uprawnień']);
        exit;
    }
}

$input = [];
if (PHP_SAPI === 'cli') {
    foreach (array_slice($argv ?? [], 1) as $arg) {
        if (str_starts_with($arg, '--driver_id=')) {
            $input['driver_id'] = substr($arg, 12);
        } elseif (str_starts_with($arg, '--dry_run=')) {
            $input['dry_run'] = substr($arg, 10);
        }
    }
} else {
    $input = $_POST;
}

$filterDriverId = isset($input['driver_id']) ? trim((string)$input['driver_id']) : '';
$dryRun = isset($input['dry_run']) ? (int)$input['dry_run'] === 1 : false;


function is_hotel20_source(string $source): bool {
    $normalized = strtolower(trim($source));
    $normalized = str_replace(' ', '', $normalized);
    return $normalized === 'hotel[20]';
}

function calculate_hotel20_final_amount(float $amount, string $type, array $terms): float {
    $percentTurnover = isset($terms['percentTurnover']) ? (float)$terms['percentTurnover'] : 0.0;

    $hotelBaseAmount = $amount - 20;
    $finalAmount = 0.0;

    if ($type === 'Karta') {
        $finalAmount = $hotelBaseAmount * ($percentTurnover / 100);
    } elseif ($type === 'Gotówka') {
        $finalAmount = -($hotelBaseAmount * (1 - ($percentTurnover / 100)));
    } elseif ($type === 'Voucher') {
        $finalAmount = $hotelBaseAmount * ($percentTurnover / 100);
    }

    return round($finalAmount, 2);
}

try {
    $pdo->beginTransaction();

    $driversSql = "
        SELECT DISTINCT k.id
        FROM kierowcy k
        INNER JOIN kursy r ON r.driver_id = k.id
        WHERE REPLACE(LOWER(TRIM(r.source)), ' ', '') = 'hotel[20]'
    ";

    $params = [];
    if ($filterDriverId !== '') {
        $driversSql .= " AND k.id = ?";
        $params[] = $filterDriverId;
    }

    $driversSql .= ' ORDER BY k.id';

    $driversStmt = $pdo->prepare($driversSql);
    $driversStmt->execute($params);
    $driverIds = $driversStmt->fetchAll(PDO::FETCH_COLUMN);

    $summary = [
        'drivers_scanned' => count($driverIds),
        'rides_updated' => 0,
        'saldo_rows_updated' => 0,
        'drivers_saldo_updated' => 0,
        'voucher_buckets_updated' => 0,
        'total_saldo_delta' => 0.0,
        'per_driver' => [],
    ];

    foreach ($driverIds as $driverId) {
        $driverLockStmt = $pdo->prepare("SELECT id, saldo, voucher_current_amount, voucher_current_month, voucher_previous_amount, voucher_previous_month FROM kierowcy WHERE id = ? FOR UPDATE");
        $driverLockStmt->execute([$driverId]);
        $driver = $driverLockStmt->fetch(PDO::FETCH_ASSOC);
        if (!$driver) {
            continue;
        }

        $driver = voucher_refresh_buckets($pdo, $driver);

        $termsStmt = $pdo->prepare('SELECT term_name, term_value FROM collaboration_terms WHERE driver_id = ?');
        $termsStmt->execute([$driverId]);
        $terms = $termsStmt->fetchAll(PDO::FETCH_KEY_PAIR);

        $ridesStmt = $pdo->prepare("SELECT id, amount, type, source, saldo_wplyw, saldo_po, date FROM kursy WHERE driver_id = ? ORDER BY date ASC, id ASC FOR UPDATE");
        $ridesStmt->execute([$driverId]);
        $rides = $ridesStmt->fetchAll(PDO::FETCH_ASSOC);

        $runningDelta = 0.0;
        $driverSaldoDelta = 0.0;
        $voucherCurrentDelta = 0.0;
        $voucherPreviousDelta = 0.0;
        $rideUpdates = 0;
        $saldoPoUpdates = 0;
        $hotel20RidesScanned = 0;

        foreach ($rides as $ride) {
            $oldSaldoWplyw = round((float)$ride['saldo_wplyw'], 2);
            $deltaForRide = 0.0;

            if (is_hotel20_source((string)$ride['source'])) {
                $hotel20RidesScanned++;
                $newSaldoWplyw = calculate_hotel20_final_amount((float)$ride['amount'], (string)$ride['type'], $terms);
                $deltaForRide = round($newSaldoWplyw - $oldSaldoWplyw, 2);

                if (abs($deltaForRide) >= 0.01) {
                    if (!$dryRun) {
                        $updateRide = $pdo->prepare('UPDATE kursy SET saldo_wplyw = ? WHERE id = ?');
                        $updateRide->execute([$newSaldoWplyw, $ride['id']]);
                    }
                    $rideUpdates++;

                    if (strtolower((string)$ride['type']) !== 'voucher') {
                        $driverSaldoDelta = round($driverSaldoDelta + $deltaForRide, 2);
                    } else {
                        $rideMonth = (new DateTime((string)$ride['date']))->format('Y-m');
                        $bucket = voucher_bucket_for_month($driver, $rideMonth);
                        if ($bucket === 'current') {
                            $voucherCurrentDelta = round($voucherCurrentDelta + $deltaForRide, 2);
                        } elseif ($bucket === 'previous') {
                            $voucherPreviousDelta = round($voucherPreviousDelta + $deltaForRide, 2);
                        }
                    }
                }
            }

            if (strtolower((string)$ride['type']) !== 'voucher') {
                $runningDelta = round($runningDelta + $deltaForRide, 2);
                if (abs($runningDelta) >= 0.01) {
                    $newSaldoPo = round((float)$ride['saldo_po'] + $runningDelta, 2);
                    if (!$dryRun) {
                        $updateSaldoPo = $pdo->prepare('UPDATE kursy SET saldo_po = ? WHERE id = ?');
                        $updateSaldoPo->execute([$newSaldoPo, $ride['id']]);
                    }
                    $saldoPoUpdates++;
                }
            }
        }

        if (abs($driverSaldoDelta) >= 0.01) {
            $newDriverSaldo = round((float)$driver['saldo'] + $driverSaldoDelta, 2);
            if (!$dryRun) {
                $updateDriverSaldo = $pdo->prepare('UPDATE kierowcy SET saldo = ? WHERE id = ?');
                $updateDriverSaldo->execute([$newDriverSaldo, $driverId]);
            }
            $summary['drivers_saldo_updated']++;
            $summary['total_saldo_delta'] = round((float)$summary['total_saldo_delta'] + $driverSaldoDelta, 2);
        }

        if (abs($voucherCurrentDelta) >= 0.01 || abs($voucherPreviousDelta) >= 0.01) {
            $newVoucherCurrent = round((float)$driver['voucher_current_amount'] + $voucherCurrentDelta, 2);
            $newVoucherPrevious = round((float)$driver['voucher_previous_amount'] + $voucherPreviousDelta, 2);

            if (!$dryRun) {
                $updateVoucher = $pdo->prepare('UPDATE kierowcy SET voucher_current_amount = ?, voucher_previous_amount = ? WHERE id = ?');
                $updateVoucher->execute([$newVoucherCurrent, $newVoucherPrevious, $driverId]);
            }

            $summary['voucher_buckets_updated']++;
        }

        $summary['rides_updated'] += $rideUpdates;
        $summary['saldo_rows_updated'] += $saldoPoUpdates;
        $summary['per_driver'][] = [
            'driver_id' => $driverId,
            'rides_updated' => $rideUpdates,
            'saldo_rows_updated' => $saldoPoUpdates,
            'driver_saldo_delta' => $driverSaldoDelta,
            'voucher_current_delta' => $voucherCurrentDelta,
            'voucher_previous_delta' => $voucherPreviousDelta,
            'hotel20_rides_scanned' => $hotel20RidesScanned,
        ];
    }

    if ($dryRun) {
        $pdo->rollBack();
    } else {
        $pdo->commit();
    }

    echo json_encode([
        'status' => 'success',
        'dry_run' => $dryRun,
        'summary' => $summary,
    ]);
    exit;
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }

    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd: ' . $e->getMessage()]);
    exit;
}
