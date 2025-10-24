<?php

function voucher_get_month_keys(): array {
    $current = new DateTime('first day of this month');
    $currentKey = $current->format('Y-m');
    $previousKey = $current->modify('-1 month')->format('Y-m');
    return [$currentKey, $previousKey];
}

function voucher_refresh_buckets(PDO $pdo, array $driver): array {
    [$currentMonth, $previousMonth] = voucher_get_month_keys();

    $currentKey = $driver['voucher_current_month'] ?? null;
    $previousKey = $driver['voucher_previous_month'] ?? null;
    $currentAmount = isset($driver['voucher_current_amount']) ? (float)$driver['voucher_current_amount'] : 0.0;
    $previousAmount = isset($driver['voucher_previous_amount']) ? (float)$driver['voucher_previous_amount'] : 0.0;

    $needsUpdate = false;

    if ($currentKey !== $currentMonth) {
        if ($currentKey === $previousMonth) {
            $previousAmount = $currentAmount;
            $previousKey = $currentKey;
        } else {
            $previousAmount = 0.0;
            $previousKey = $previousMonth;
        }
        $currentAmount = 0.0;
        $currentKey = $currentMonth;
        $needsUpdate = true;
    }

    if ($previousKey !== $previousMonth) {
        $previousKey = $previousMonth;
        $needsUpdate = true;
    }

    if ($needsUpdate) {
        $stmt = $pdo->prepare("UPDATE kierowcy SET voucher_current_amount = ?, voucher_current_month = ?, voucher_previous_amount = ?, voucher_previous_month = ? WHERE id = ?");
        $stmt->execute([
            round($currentAmount, 2),
            $currentKey,
            round($previousAmount, 2),
            $previousKey,
            $driver['id']
        ]);

        $driver['voucher_current_amount'] = $currentAmount;
        $driver['voucher_current_month'] = $currentKey;
        $driver['voucher_previous_amount'] = $previousAmount;
        $driver['voucher_previous_month'] = $previousKey;
    }

    return $driver;
}

function voucher_increment_bucket(PDO $pdo, string $driverId, array $driver, float $amount, string $bucket): array {
    if (abs($amount) < 1e-6) {
        return $driver;
    }

    $amount = round($amount, 2);

    if ($bucket === 'current') {
        $currentAmount = isset($driver['voucher_current_amount']) ? (float)$driver['voucher_current_amount'] : 0.0;
        $currentAmount = round($currentAmount + $amount, 2);
        $stmt = $pdo->prepare("UPDATE kierowcy SET voucher_current_amount = ? WHERE id = ?");
        $stmt->execute([$currentAmount, $driverId]);
        $driver['voucher_current_amount'] = $currentAmount;
    } elseif ($bucket === 'previous') {
        $previousAmount = isset($driver['voucher_previous_amount']) ? (float)$driver['voucher_previous_amount'] : 0.0;
        $previousAmount = round($previousAmount + $amount, 2);
        $stmt = $pdo->prepare("UPDATE kierowcy SET voucher_previous_amount = ? WHERE id = ?");
        $stmt->execute([$previousAmount, $driverId]);
        $driver['voucher_previous_amount'] = $previousAmount;
    }

    return $driver;
}

function voucher_bucket_for_month(array $driver, string $yearMonth): string {
    if (($driver['voucher_current_month'] ?? null) === $yearMonth) {
        return 'current';
    }
    if (($driver['voucher_previous_month'] ?? null) === $yearMonth) {
        return 'previous';
    }
    [$currentMonth, $previousMonth] = voucher_get_month_keys();
    if ($yearMonth === $currentMonth) {
        return 'current';
    }
    if ($yearMonth === $previousMonth) {
        return 'previous';
    }
    return 'current';
}
