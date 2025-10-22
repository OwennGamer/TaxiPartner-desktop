<?php
require_once __DIR__ . '/jwt_utils.php';
require_once __DIR__ . '/db.php';

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Niedozwolona metoda"]);
    exit;
}

$deviceId = $_SERVER['HTTP_DEVICE_ID'] ?? '';
if (!$deviceId) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Brak nagłówka Device-Id"]);
    exit;
}

$rawBody = file_get_contents('php://input');
$payload = json_decode($rawBody, true);
if (!is_array($payload)) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Nieprawidłowy format danych"]);
    exit;
}

$token = getAuthorizationHeader();
$decoded = $token ? verifyJWT($token) : null;

$summary = trim((string)($payload['summary'] ?? ''));
$message = isset($payload['message']) ? trim((string)$payload['message']) : '';
if ($summary === '' && $message !== '') {
    $summary = mb_substr($message, 0, 255);
}
if ($summary === '') {
    $summary = 'Nieznany błąd aplikacji';
}

$source = strtolower((string)($payload['source'] ?? ''));
if ($source === '') {
    $source = $deviceId === 'admin_panel' ? 'desktop' : $deviceId;
}

$level = strtoupper((string)($payload['level'] ?? 'ERROR'));
$allowedLevels = ['DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL'];
if (!in_array($level, $allowedLevels, true)) {
    $level = 'ERROR';
}

$stacktrace = isset($payload['stacktrace']) && is_string($payload['stacktrace'])
    ? trim($payload['stacktrace'])
    : null;

$driverId = isset($payload['driver_id']) ? trim((string)$payload['driver_id']) : '';
if ($driverId === '' && $decoded && isset($decoded->driver_id)) {
    $driverId = (string)$decoded->driver_id;
}
if ($driverId === '' && $decoded && isset($decoded->user_id)) {
    $driverId = (string)$decoded->user_id;
}
$driverId = $driverId !== '' ? $driverId : null;

$licensePlate = isset($payload['license_plate']) ? trim((string)$payload['license_plate']) : '';
$licensePlate = $licensePlate !== '' ? $licensePlate : null;

$appVersion = isset($payload['app_version']) ? trim((string)$payload['app_version']) : null;
$metadata = null;
if (isset($payload['metadata'])) {
    $encoded = json_encode($payload['metadata'], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    if ($encoded !== false) {
        $metadata = $encoded;
    }
}

try {
    $stmt = $pdo->prepare(
        'INSERT INTO app_error_logs (source, level, summary, message, stacktrace, driver_id, license_plate, app_version, device_id, metadata) '
        . 'VALUES (:source, :level, :summary, :message, :stacktrace, :driver_id, :license_plate, :app_version, :device_id, :metadata)'
    );

    $stmt->execute([
        ':source' => $source,
        ':level' => $level,
        ':summary' => $summary,
        ':message' => $message !== '' ? $message : null,
        ':stacktrace' => $stacktrace,
        ':driver_id' => $driverId,
        ':license_plate' => $licensePlate,
        ':app_version' => $appVersion,
        ':device_id' => $deviceId,
        ':metadata' => $metadata,
    ]);

    http_response_code(201);
    echo json_encode([
        "status" => "success",
        "log_id" => (int)$pdo->lastInsertId(),
    ]);
} catch (PDOException $e) {
    error_log('log_error.php failed: ' . $e->getMessage());
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Błąd zapisu logu"]);
}
