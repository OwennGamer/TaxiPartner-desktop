<?php
// Konfiguracja bazy danych
define('DB_HOST', '127.0.0.1'); // Zmiana na localhost dla wewnętrznych połączeń
define('DB_PORT', '8443'); // Port MariaDB, upewnij się, że to właściwy port
define('DB_NAME', 'taxi_partner');
define('DB_USER', 'SQLserwer');
define('DB_PASS', 'SQL7169%');

// Firebase configuration
$googleCredentials = getenv('GOOGLE_APPLICATION_CREDENTIALS');
if ($googleCredentials === false || $googleCredentials === '') {
    http_response_code(500);
    header('Content-Type: application/json');
    echo json_encode(['error' => 'GOOGLE_APPLICATION_CREDENTIALS is not set']);
    exit;
}
define('GOOGLE_APPLICATION_CREDENTIALS', $googleCredentials);

$projectId = getenv('FIREBASE_PROJECT_ID');
if ($projectId === false || $projectId === '') {
        http_response_code(500);
    header('Content-Type: application/json');
    echo json_encode(['error' => 'FIREBASE_PROJECT_ID is not set']);
    exit;
}
define('FIREBASE_PROJECT_ID', $projectId);  // ID projektu z Firebase → Project settings → General
?>
