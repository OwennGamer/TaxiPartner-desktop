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
$vehicle_plate = trim((string)($_POST['vehicle_plate'] ?? ''));
$start_odometer = isset($_POST['start_odometer']) ? intval($_POST['start_odometer']) : null;

if ($vehicle_plate === '' || $start_odometer === null) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak wymaganych danych']);
    exit;
}

try {
    $pdo->beginTransaction();

    // Uzupełnij brakujący przebieg dla ostatniej auto-zamkniętej sesji tego pojazdu
    // (sesja zamknięta czasowo, ale bez wpisanego end_odometer).
    $stmtFillAutoClosed = $pdo->prepare(
        "UPDATE work_sessions\n" .
        "   SET end_odometer = ?\n" .
        " WHERE UPPER(REPLACE(TRIM(vehicle_plate),' ' ,'')) = ?\n" .
        "   AND end_time IS NOT NULL\n" .
        "   AND end_odometer IS NULL\n" .
        " ORDER BY end_time DESC, id DESC\n" .
        " LIMIT 1"
    );

    $normalizePlate = static function (?string $value): string {
        $normalizedValue = trim((string)$value);
        $normalizedValue = preg_replace('/\s+/', '', $normalizedValue) ?? '';

        if (function_exists('mb_strtoupper')) {
            return mb_strtoupper($normalizedValue, 'UTF-8');
        }

        return strtoupper($normalizedValue);
    };

    $normalizedVehiclePlate = $normalizePlate($vehicle_plate);
    
    $stmtFillAutoClosed->execute([
        $start_odometer,
        $normalizedVehiclePlate,
    ]);

    $normalizeId = static function (?string $value): string {
        $normalizedValue = trim((string)$value);

        if (function_exists('mb_strtoupper')) {
            return mb_strtoupper($normalizedValue, 'UTF-8');
        }

        return strtoupper($normalizedValue);
    };

    // Sprawdzenie ostatniego kierowcy, który jeździł tym pojazdem
    $stmtLastVehicleDriver = $pdo->prepare(
        "SELECT driver_id FROM work_sessions WHERE UPPER(REPLACE(TRIM(vehicle_plate),' ' ,'')) = ? ORDER BY COALESCE(end_time, start_time) DESC, id DESC LIMIT 1"
    );
    $stmtLastVehicleDriver->execute([$normalizedVehiclePlate]);
    $lastVehicleDriver = $stmtLastVehicleDriver->fetchColumn();

    // Inwentaryzacja wymagana, gdy kierowca się zmienia lub brak historii dla pojazdu
    $requiresInventory = $lastVehicleDriver === false
        || $normalizeId($lastVehicleDriver) !== $normalizeId($driver_id);

    // Automatyczne zamknięcie otwartej sesji poprzedniego kierowcy na tym samym pojeździe
    $stmtOpenVehicleSession = $pdo->prepare(
        "SELECT id, driver_id FROM work_sessions
         WHERE UPPER(REPLACE(TRIM(vehicle_plate),' ' ,'')) = ?
           AND end_time IS NULL
         ORDER BY start_time DESC, id DESC
         LIMIT 1"
    );
    $stmtOpenVehicleSession->execute([$normalizedVehiclePlate]);
    $openVehicleSession = $stmtOpenVehicleSession->fetch(PDO::FETCH_ASSOC);

    $autoClosedSessionId = null;
    if (
        $openVehicleSession
        && $normalizeId($openVehicleSession['driver_id']) !== $normalizeId($driver_id)
    ) {
        $stmtAutoCloseSession = $pdo->prepare(
            "UPDATE work_sessions
             SET end_time = NOW(), end_odometer = NULL
             WHERE id = ?"
        );
        $stmtAutoCloseSession->execute([
            
            (int)$openVehicleSession['id'],
        ]);
        $autoClosedSessionId = (int)$openVehicleSession['id'];
    }

    $stmtDriver = $pdo->prepare("SELECT imie, nazwisko FROM kierowcy WHERE id = ? LIMIT 1");
    $stmtDriver->execute([$driver_id]);
    $driverData = $stmtDriver->fetch(PDO::FETCH_ASSOC) ?: [];

    $driverName = trim(sprintf(
        '%s %s',
        (string)($driverData['imie'] ?? ''),
        (string)($driverData['nazwisko'] ?? '')
    ));

    if ($driverName === '') {
        $driverName = 'Nieznany kierowca';
    }
    

    // Sprawdzenie poprzedniego pojazdu
    $stmtPrev = $pdo->prepare("SELECT vehicle_plate FROM work_sessions WHERE driver_id = ? ORDER BY start_time DESC LIMIT 1");
    $stmtPrev->execute([$driver_id]);
    $prevVehicle = $stmtPrev->fetchColumn();

    // Jeśli zmieniono pojazd — wyślij e-mail
    if ($prevVehicle && $normalizePlate($prevVehicle) !== $normalizedVehiclePlate) {
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
                $mail->Subject = "Zmiana samochodu przez kierowcę $driver_id ($driverName)";
                $mail->Body = "Kierowca <b>$driver_id ($driverName)</b> zmienił samochód z <b>$prevVehicle</b> na <b>$vehicle_plate</b>.";

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
