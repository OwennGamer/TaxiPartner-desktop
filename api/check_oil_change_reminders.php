<?php
/**
 * Cron: przypomnienia o wymianie oleju (mail + push FCM).
 */
require_once __DIR__ . '/db.php';
require_once __DIR__ . '/mailer_utils.php';
require_once __DIR__ . '/fcm_v1.php';

use PHPMailer\PHPMailer\PHPMailer;

const OIL_ALERT_EMAIL = [
    'serwis@taxi-partner.com.pl',
    'pawel.turek330@gmail.com'
];

function pickReminderType(?int $daysLeft, ?int $kmLeft): ?string
{
    $isDateDue = $daysLeft !== null && $daysLeft <= 7;
    $isKmDue = $kmLeft !== null && $kmLeft <= 500;

    if (!$isDateDue && !$isKmDue) {
        return null;
    }
    if ($isDateDue && !$isKmDue) {
        return 'date';
    }
    if ($isKmDue && !$isDateDue) {
        return 'mileage';
    }

    $dateRatio = $daysLeft !== null ? max(0, min(1, $daysLeft / 7)) : 1;
    $kmRatio = $kmLeft !== null ? max(0, min(1, $kmLeft / 500)) : 1;
    return $dateRatio <= $kmRatio ? 'date' : 'mileage';
}

function sendOilEmail(array $vehicle, string $triggerType): void
{
    if (!loadMailer()) {
        error_log('PHPMailer unavailable – pominięto wysyłkę maila o wymianie oleju.');
        return;
    }

    $plate = $vehicle['rejestracja'] ?? '';
    $marka = $vehicle['marka'] ?? '';
    $model = $vehicle['model'] ?? '';
    $triggerText = $triggerType === 'date' ? 'za 7 dni' : 'za 500km';

    $subject = 'ZBLIŻA SIĘ TERMIN WYMIANY OLEJU "' . $plate . '"';
    $body = 'Pojazd marki "' . $marka . '" "' . $model . '" o numerze rejestracyjnym "' . $plate
        . '" wymaga wymiany oleju ' . $triggerText . '.';

    $mail = new PHPMailer(true);
    $mail->isSMTP();
    $mail->Host = getenv('SMTP_HOST') ?: 'smtp.gmail.com';
    $mail->SMTPAuth = true;
    $mail->Username = getenv('SMTP_USER') ?: 'partnertaxi.biuro@gmail.com';
    $mail->Password = getenv('SMTP_PASS') ?: 'sapy skyn exdr trwv';
    $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
    $mail->Port = getenv('SMTP_PORT') ?: 587;
    $mail->CharSet = 'UTF-8';

    $mail->setFrom($mail->Username, 'Taxi Partner');

    // ✅ POPRAWKA: wiele maili
    foreach (OIL_ALERT_EMAIL as $email) {
        $mail->addAddress(trim($email));
    }

    $mail->isHTML(false);
    $mail->Subject = $subject;
    $mail->Body = $body;

    $mail->send();
}

function sendOilPush(PDO $pdo, array $vehicle, string $triggerType): int
{
    $plate = $vehicle['rejestracja'] ?? '';
    $marka = $vehicle['marka'] ?? '';
    $model = $vehicle['model'] ?? '';
    $triggerText = $triggerType === 'date' ? 'za 7 dni' : 'za 500km';

    $title = 'ZBLIŻA SIĘ TERMIN WYMIANY OLEJU "' . $plate . '"';
    $body = 'Pojazd marki "' . $marka . '" "' . $model . '" o numerze rejestracyjnym "' . $plate
        . '" wymaga wymiany oleju ' . $triggerText . '.';

    $stmt = $pdo->query("
        SELECT fcm_token
        FROM kierowcy
        WHERE rola IN ('flotowiec', 'admin', 'administrator')
          AND fcm_token IS NOT NULL
          AND fcm_token <> ''
    ");

    $tokens = $stmt->fetchAll(PDO::FETCH_COLUMN);
    $sent = 0;

    foreach ($tokens as $token) {
        try {
            $resp = sendFcmV1((string)$token, $title, $body, [
                'type' => 'oil_change_reminder',
                'vehicle_plate' => $plate,
                'trigger' => $triggerType
            ]);
            if ($resp !== null) {
                $sent++;
            }
        } catch (Throwable $e) {
            error_log('FCM oil reminder error: ' . $e->getMessage());
        }
    }

    return $sent;
}

try {
    $today = new DateTimeImmutable('today');

    $stmt = $pdo->query("
        SELECT id, rejestracja, marka, model, przebieg, wymiana_oleju_data, wymiana_oleju_przebieg,
               oil_reminder_sent_at, oil_reminder_sent_type
        FROM pojazdy
        WHERE wymiana_oleju_data IS NOT NULL OR wymiana_oleju_przebieg IS NOT NULL
    ");

    $vehicles = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $updateSent = $pdo->prepare("
        UPDATE pojazdy
        SET oil_reminder_sent_at = NOW(),
            oil_reminder_sent_type = :type
        WHERE id = :id
    ");

    $clearSent = $pdo->prepare("
        UPDATE pojazdy
        SET oil_reminder_sent_at = NULL,
            oil_reminder_sent_type = NULL
        WHERE id = :id
    ");

    foreach ($vehicles as $vehicle) {

        $daysLeft = null;
        if (!empty($vehicle['wymiana_oleju_data'])) {
            $dueDate = DateTimeImmutable::createFromFormat('Y-m-d', (string)$vehicle['wymiana_oleju_data']);
            if ($dueDate !== false) {
                $daysLeft = (int)$today->diff($dueDate)->format('%r%a');
            }
        }

        $kmLeft = null;
        if ($vehicle['wymiana_oleju_przebieg'] !== null && $vehicle['wymiana_oleju_przebieg'] !== '') {
            $kmLeft = (int)$vehicle['wymiana_oleju_przebieg'] - (int)$vehicle['przebieg'];
        }

        $triggerType = pickReminderType($daysLeft, $kmLeft);

        if ($triggerType === null) {
            if (!empty($vehicle['oil_reminder_sent_at'])) {
                $clearSent->execute([':id' => (int)$vehicle['id']]);
            }
            continue;
        }

        if (!empty($vehicle['oil_reminder_sent_at'])) {
            continue;
        }

        try {
            sendOilEmail($vehicle, $triggerType);
        } catch (Throwable $e) {
            error_log('Oil email error: ' . $e->getMessage());
        }

        sendOilPush($pdo, $vehicle, $triggerType);

        $updateSent->execute([
            ':id' => (int)$vehicle['id'],
            ':type' => $triggerType
        ]);
    }

    echo json_encode(['status' => 'success', 'checked' => count($vehicles)], JSON_UNESCAPED_UNICODE);

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => $e->getMessage()], JSON_UNESCAPED_UNICODE);
}
