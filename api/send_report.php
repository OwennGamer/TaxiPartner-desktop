<?php
header('Content-Type: application/json; charset=utf-8');

require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/mailer_utils.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Method not allowed']);
    exit;
}

$jwt = getAuthenticatedJwt();
$type = strtoupper(trim($_POST['type'] ?? ''));
$description = trim($_POST['description'] ?? '');
$vehiclePlate = trim($_POST['vehicle_plate'] ?? '');
$driverId = trim($_POST['driver_id'] ?? '');

if ($driverId === '' && $jwt && isset($jwt->user_id)) {
    $driverId = (string)$jwt->user_id;
}

$allowedTypes = ['SERWIS', 'INNE'];
if (!in_array($type, $allowedTypes, true)) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowy typ zgłoszenia']);
    exit;
}

if ($description === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak opisu zgłoszenia']);
    exit;
}

if ($vehiclePlate === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak numeru rejestracyjnego']);
    exit;
}

if (!loadMailer()) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Mailer unavailable']);
    exit;
}

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
    $mail->addAddress('biuro@taxi-partner.com.pl');

    if (!empty($_FILES['photo']) && $_FILES['photo']['error'] === UPLOAD_ERR_OK) {
        $safeName = 'zalacznik_' . preg_replace('/[^A-Za-z0-9_.-]/', '_', $_FILES['photo']['name']);
        $mail->addAttachment($_FILES['photo']['tmp_name'], $safeName);
    }

    $mail->isHTML(true);
    $subjectType = preg_replace("/[\r\n]+/", ' ', $type);
    $driverLabel = $driverId !== '' ? $driverId : 'nieznany';
    $plateLabel = $vehiclePlate !== '' ? $vehiclePlate : 'nieznany';

    $driverSubject = preg_replace("/[\r\n]+/", ' ', $driverLabel);
    $plateSubject = preg_replace("/[\r\n]+/", ' ', $plateLabel);

    $mail->Subject = 'Nowe zgłoszenie ' . $subjectType
        . ' | Kierowca: ' . $driverSubject
        . ' | Rejestracja: ' . $plateSubject;

    $body = '<p>Typ zgłoszenia: <strong>' . escapeHtml($subjectType) . '</strong></p>';
    $body .= '<p>Kierowca ID: <strong>' . escapeHtml($driverLabel) . '</strong></p>';
    $body .= '<p>Numer rejestracyjny: <strong>' . escapeHtml($plateLabel) . '</strong></p>';
    $body .= '<p>Opis:<br>' . nl2br(escapeHtml($description)) . '</p>';

    $mail->Body = $body;

    $mail->send();

    echo json_encode(['status' => 'success', 'message' => 'Zgłoszenie wysłane']);
} catch (Exception $e) {
    error_log('Błąd wysyłania zgłoszenia: ' . $e->getMessage());
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Nie udało się wysłać zgłoszenia']);
}

function escapeHtml(string $value): string
{
    return htmlspecialchars($value, ENT_QUOTES | ENT_SUBSTITUTE, 'UTF-8');
}
