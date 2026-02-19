<?php
require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/mailer_utils.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : false;
if (!$decoded) {
    http_response_code(401);
    echo json_encode(['status' => 'error', 'message' => 'Brak ważnego tokena']);
    exit;
}

$driver_id = $decoded->user_id;
$vehicle_plate = trim($_POST['vehicle_plate'] ?? '');
$start_odometer = isset($_POST['start_odometer']) ? intval($_POST['start_odometer']) : null;

if ($vehicle_plate === '' || $start_odometer === null) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

try {
    $pdo->beginTransaction();

    $normalizeId = static function (?string $value): string {
        $normalizedValue = trim((string)$value);

        if (function_exists('mb_strtoupper')) {
            return mb_strtoupper($normalizedValue, 'UTF-8');
        }

        return strtoupper($normalizedValue);
    };

    // Sprawdzenie ostatniego kierowcy, który jeździł tym pojazdem
    $stmtLastVehicleDriver = $pdo->prepare(
        "SELECT driver_id FROM work_sessions WHERE LOWER(vehicle_plate) = LOWER(?) ORDER BY start_time DESC, id DESC LIMIT 1"
    );
    $stmtLastVehicleDriver->execute([$vehicle_plate]);
    $lastVehicleDriver = $stmtLastVehicleDriver->fetchColumn();

    // Inwentaryzacja wymagana, gdy kierowca się zmienia lub brak historii dla pojazdu
    $requiresInventory = $lastVehicleDriver === false
        || $normalizeId($lastVehicleDriver) !== $normalizeId($driver_id);

    // Automatyczne zamknięcie otwartej sesji poprzedniego kierowcy na tym samym pojeździe
    $stmtOpenVehicleSession = $pdo->prepare(
        "SELECT id, driver_id FROM work_sessions
         WHERE LOWER(vehicle_plate) = LOWER(?)
           AND end_time IS NULL
         ORDER BY start_time DESC, id DESC
         LIMIT 1"
    );
    $stmtOpenVehicleSession->execute([$vehicle_plate]);
    $openVehicleSession = $stmtOpenVehicleSession->fetch(PDO::FETCH_ASSOC);

    $autoClosedSessionId = null;
    if (
        $openVehicleSession
        && $normalizeId($openVehicleSession['driver_id']) !== $normalizeId($driver_id)
    ) {
        $stmtAutoCloseSession = $pdo->prepare(
            "UPDATE work_sessions
             SET end_time = NOW(), end_odometer = ?
             WHERE id = ?"
        );
        $stmtAutoCloseSession->execute([
            $start_odometer,
            (int)$openVehicleSession['id'],
        ]);
        $autoClosedSessionId =

    // Sprawdzenie poprzedniego pojazdu
    $stmtPrev = $pdo->prepare("SELECT vehicle_plate FROM work_sessions WHERE driver_id = ? ORDER BY start_time DESC LIMIT 1");
    $stmtPrev->execute([$driver_id]);
    $prevVehicle = $stmtPrev->fetchColumn();

    // Jeśli zmieniono pojazd — wyślij e-mail
    if ($prevVehicle && $prevVehicle !== $vehicle_plate) {
        $mailerAvailable = loadMailer();
        if ($mailerAvailable) {
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
                $mail->addAddress('bok@taxi-partner.com.pl');

                $mail->isHTML(true);
                $mail->Subject = "TEST!!! Zmiana samochodu przez kierowcę $driver_id !!!TEST";
                $mail->Body = "Kierowca <b>$driver_id</b> zmienił samochód z <b>$prevVehicle</b> na <b>$vehicle_plate</b>.";

                $mail->send();
            } catch (Exception $e) {
                error_log("Błąd przy wysyłaniu maila: {$mail->ErrorInfo}");
            }
        } else {
            error_log('PHPMailer unavailable – pominięto wysłanie informacji o zmianie pojazdu.');
        }
    }

    // Zapis rozpoczęcia pracy
    $stmt = $pdo->prepare(
        "INSERT INTO work_sessions (driver_id, vehicle_plate, start_time, start_odometer) VALUES (?, ?, NOW(), ?)"
    );
    $stmt->execute([$driver_id, $vehicle_plate, $start_odometer]);
    $id = $pdo->lastInsertId();
    
    $pdo->commit();

    echo json_encode([
        'status' => 'success',
        'session_id' => (int)$id,
        'require_inventory' => $requiresInventory,
        'auto_closed_session_id' => $autoClosedSessionId,
    ]);
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
