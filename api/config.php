<?php
// =======================
// Konfiguracja bazy danych
// =======================
define('DB_HOST', '127.0.0.1');   // u Ciebie tak było – zostawiam
define('DB_PORT', '8443');        // u Ciebie tak było – zostawiam (standard to 3306, ale nie zmieniam)
define('DB_NAME', 'taxi_partner');
define('DB_USER', 'SQLserwer');
define('DB_PASS', 'SQL7169%');

// =======================
// Firebase Cloud Messaging (HTTP v1)
// =======================
// ID projektu Firebase – spróbuj wziąć z ENV (np. FIREBASE_PROJECT_ID), a jak brak to stała:
define('FCM_PROJECT_ID', getenv('FIREBASE_PROJECT_ID') ?: 'partner-taxi-kierowca-2e764');

// Ścieżka do konta serwisowego (ENV ustawione w Apache) z bezpiecznym fallbackiem do pliku w katalogu API:
define('GOOGLE_APPLICATION_CREDENTIALS', getenv('GOOGLE_APPLICATION_CREDENTIALS') ?: __DIR__ . '/partner-taxi-kierowca-2e764-firebase-adminsdk-fbsvc-3f8d240139.json');

// Ścieżka do logu FCM (diagnostyka wysyłek):
define('FCM_LOG_PATH', __DIR__ . '/debug_fcm.log');

// Legacy (niewykorzystywane przy HTTP v1) – zostawiamy pusty dla zgodności z istniejącym kodem:
if (!defined('FCM_SERVER_KEY')) {
    define('FCM_SERVER_KEY', '');
}




