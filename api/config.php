<?php
// Konfiguracja bazy danych
define('DB_HOST', '127.0.0.1'); // Zmiana na localhost dla wewnętrznych połączeń
define('DB_PORT', '8443'); // Port MariaDB, upewnij się, że to właściwy port
define('DB_NAME', 'taxi_partner');
define('DB_USER', 'SQLserwer');
define('DB_PASS', 'SQL7169%');

define('GOOGLE_APPLICATION_CREDENTIALS', __DIR__ . '/partner-taxi-kierowca-2e764-firebase-adminsdk-fbsvc-3f8d240139.json');
define('FIREBASE_PROJECT_ID', 'partner-taxi-kierowca-2e764');  // ID projektu z Firebase → Project settings → General
define('FCM_SERVER_KEY', getenv('FCM_SERVER_KEY') ?: '');
?>
