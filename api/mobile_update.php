<?php
// /var/www/html/api/mobile_update.php
// Prosty endpoint do testu mechanizmu aktualizacji mobilnej.
// Zwraca JSON zgodny z MobileUpdateResponse aplikacji.

header('Content-Type: application/json; charset=utf-8');

// KONFIGURACJA TESTOWA — DOSTOSUJ
$latestVersion   = "1.1.7";        // docelowy versionName (musi być "większy" niż 1.0)
$mandatory       = true;           // scenariusz: aktualizacja WYMUSZONA
$apkFileName     = "app-1.1.7.apk";// nazwa pliku, który wrzucisz do /api/updates
$changelog       = "• Dodanie funkcji ZGŁOSZENIA ";

// ZŁÓŻ URL do pliku APK (port 8444 zgodnie z Twoim BASE_URL)
$schemeHostPort  = "http://164.126.143.20:8444";
$apkUrl          = $schemeHostPort . "/api/updates/" . $apkFileName;

// Walidacja minimalna
if (!preg_match('/^\d+(\.\d+){1,2}$/', $latestVersion)) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "Bad server version format"
    ]);
    exit;
}

// Odpowiedź zgodna z modelem w apce
$response = [
    "status" => "success",
    "data" => [
        "version"   => $latestVersion,   // np. "1.1.0"
        "url"       => $apkUrl,          // pełny URL do APK
        "changelog" => $changelog,       // tekst do wyświetlenia w dialogu
        "mandatory" => $mandatory        // true/false
    ]
];

echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
