<?php
require_once __DIR__ . '/db.php';
require_once __DIR__ . '/auth.php';

require_once __DIR__ . '/phpmailer/PHPMailer-master/src/Exception.php';
require_once __DIR__ . '/phpmailer/PHPMailer-master/src/PHPMailer.php';
require_once __DIR__ . '/phpmailer/PHPMailer-master/src/SMTP.php';

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
    // Sprawdzenie poprzedniego pojazdu
    $stmtPrev = $pdo->prepare("SELECT vehicle_plate FROM work_sessions WHERE driver_id = ? ORDER BY start_time DESC LIMIT 1");
    $stmtPrev->execute([$driver_id]);
    $prevVehicle = $stmtPrev->fetchColumn();

    // Jeśli zmieniono pojazd — wyślij e-mail
    if ($prevVehicle && $prevVehicle !== $vehicle_plate) {
        $mail = new PHPMailer(true);
        try {
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
    }

    // Zapis rozpoczęcia pracy
    $stmt = $pdo->prepare(
        "INSERT INTO work_sessions (driver_id, vehicle_plate, start_time, start_odometer) VALUES (?, ?, NOW(), ?)"
    );
    $stmt->execute([$driver_id, $vehicle_plate, $start_odometer]);
    $id = $pdo->lastInsertId();

    echo json_encode(['status' => 'success', 'session_id' => (int)$id]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}

