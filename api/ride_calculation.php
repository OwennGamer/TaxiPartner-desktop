<?php

if (!function_exists('calculate_ride_saldo_impact')) {
    function calculate_ride_saldo_impact(float $amount, string $type, string $source, array $terms): float {
        $percentTurnover = isset($terms['percentTurnover']) ? (float)$terms['percentTurnover'] : 0;
        $cardCommission = isset($terms['cardCommission']) ? (float)$terms['cardCommission'] : 0;
        $partnerCommission = isset($terms['partnerCommission']) ? (float)$terms['partnerCommission'] : 0;
        $boltCommission = isset($terms['boltCommission']) ? (float)$terms['boltCommission'] : 0;

        $driverShare = $percentTurnover / 100;
        $companyShare = 1 - $driverShare;
        $finalAmount = 0;

        if ($source === "Postój") {
            if ($type === "Karta") {
                $afterCard = $amount - ($amount * ($cardCommission / 100));
                $finalAmount = $afterCard * $driverShare;
            } elseif ($type === "Gotówka") {
                $finalAmount = -($amount * $companyShare);
            } elseif ($type === "Voucher") {
                $afterPartner = $amount - ($amount * ($partnerCommission / 100));
                $finalAmount = $afterPartner * $driverShare;
            }
        } elseif ($source === "Dyspozytornia") {
            if ($type === "Karta") {
                $afterCommissions = $amount - ($amount * ($cardCommission / 100)) - ($amount * ($partnerCommission / 100));
                $finalAmount = $afterCommissions * $driverShare;
            } elseif ($type === "Gotówka") {
                $afterPartner = $amount - ($amount * ($partnerCommission / 100));
                $finalAmount = -(($amount * ($partnerCommission / 100)) + ($afterPartner * $companyShare));
            } elseif ($type === "Voucher") {
                $afterPartner = $amount - ($amount * ($partnerCommission / 100));
                $finalAmount = $afterPartner * $driverShare;
            }
        } elseif ($source === "Hotel[20]") {
            $hotelBaseAmount = max(0, $amount - 20);
            if ($type === "Karta") {
                $afterCard = $hotelBaseAmount - ($hotelBaseAmount * ($cardCommission / 100));
                $finalAmount = $afterCard * $driverShare;
            } elseif ($type === "Gotówka") {
                $finalAmount = -($hotelBaseAmount * $companyShare);
            } elseif ($type === "Voucher") {
                $finalAmount = $hotelBaseAmount * $driverShare;
            }
        } elseif ($source === "Hotel[10]") {
            $hotelBaseAmount = max(0, $amount - 10);
            if ($type === "Karta") {
                $afterCard = $hotelBaseAmount - ($hotelBaseAmount * ($cardCommission / 100));
                $finalAmount = $afterCard * $driverShare;
            } elseif ($type === "Gotówka") {
                $finalAmount = -($hotelBaseAmount * $companyShare);
            } elseif ($type === "Voucher") {
                $finalAmount = $hotelBaseAmount * $driverShare;
            }
        } elseif ($source === "Bolt") {
            $boltBaseAmount = max(0, $amount - 20);
            if ($type === "Karta") {
                $afterBolt = $boltBaseAmount - ($boltBaseAmount * ($boltCommission / 100));
                $finalAmount = $afterBolt * $driverShare;
            } elseif ($type === "Gotówka") {
                $afterBolt = $boltBaseAmount - ($boltBaseAmount * ($boltCommission / 100));
                $finalAmount = -(($boltBaseAmount * ($boltCommission / 100)) + ($afterBolt * $companyShare));
            } elseif ($type === "Voucher") {
                $afterBolt = $boltBaseAmount - ($boltBaseAmount * ($boltCommission / 100));
                $finalAmount = $afterBolt * $driverShare;
            }
        }

        $rounded = round($finalAmount, 2);
        return abs($rounded) < 0.005 ? 0.0 : $rounded;
    }
}
