<?php
// fcm_v1.php — wysyłka FCM HTTP v1 z OAuth2 (konto serwisowe JSON)
require_once __DIR__ . '/config.php';

/** Base64 URL-safe (bez =, +, /) */
function b64url($data) {
    return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
}

/** Zwraca access_token Google OAuth2 dla scope firebase.messaging */
function getGoogleAccessToken(): string {
    $jsonPath = GOOGLE_APPLICATION_CREDENTIALS;
    if (!file_exists($jsonPath)) {
        throw new Exception("Brak pliku konta serwisowego: $jsonPath");
    }
    $sa = json_decode(file_get_contents($jsonPath), true, 512, JSON_THROW_ON_ERROR);

    $now = time();
    $header = ['alg' => 'RS256', 'typ' => 'JWT'];
    $claim = [
        'iss'   => $sa['client_email'],
        'scope' => 'https://www.googleapis.com/auth/firebase.messaging',
        'aud'   => 'https://oauth2.googleapis.com/token',
        'iat'   => $now,
        'exp'   => $now + 3600,
    ];

    $jwtUnsigned = b64url(json_encode($header)) . '.' . b64url(json_encode($claim));
    $privateKey = openssl_pkey_get_private($sa['private_key']);
    if (!$privateKey) {
        throw new Exception("Nie udało się wczytać klucza prywatnego z JSON.");
    }
    $signature = '';
    if (!openssl_sign($jwtUnsigned, $signature, $privateKey, OPENSSL_ALGO_SHA256)) {
        throw new Exception("Nie udało się podpisać JWT.");
    }
    $jwt = $jwtUnsigned . '.' . b64url($signature);

    // Wymiana JWT -> access_token
    $ch = curl_init('https://oauth2.googleapis.com/token');
    $post = http_build_query([
        'grant_type' => 'urn:ietf:params:oauth:grant-type:jwt-bearer',
        'assertion'  => $jwt
    ]);
    curl_setopt_array($ch, [
        CURLOPT_POST => true,
        CURLOPT_POSTFIELDS => $post,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_HTTPHEADER => [
            'Content-Type: application/x-www-form-urlencoded'
        ],
        CURLOPT_TIMEOUT => 20,
    ]);
    $resp = curl_exec($ch);
    $http = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $err  = curl_error($ch);
    curl_close($ch);

    if ($resp === false) {
        throw new Exception("OAuth token request failed: $err");
    }
    $json = json_decode($resp, true);
    if ($http !== 200 || empty($json['access_token'])) {
        throw new Exception("OAuth token error ($http): $resp");
    }
    return $json['access_token'];
}

/** Wysyła powiadomienie FCM v1 na pojedynczy token. */
function sendFcmV1(string $deviceToken, string $title, string $body, array $data = []): array {
    $accessToken = getGoogleAccessToken();

    $url = 'https://fcm.googleapis.com/v1/projects/' . FCM_PROJECT_ID . '/messages:send';
    $payload = [
        'message' => [
            'token' => $deviceToken,
            'notification' => [
                'title' => $title,
                'body'  => $body,
            ],
            'android' => [
                'priority' => 'HIGH',
                'notification' => [
                    'channel_id' => 'taxi_notifications',
                ],
            ],
            'data' => array_map('strval', $data),
        ],
    ];

    $ch = curl_init($url);
    curl_setopt_array($ch, [
        CURLOPT_POST => true,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_HTTPHEADER => [
            'Content-Type: application/json',
            'Authorization: Bearer ' . $accessToken,
        ],
        CURLOPT_POSTFIELDS => json_encode($payload, JSON_UNESCAPED_UNICODE),
        CURLOPT_TIMEOUT => 20,
    ]);
    $resp = curl_exec($ch);
    $http = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $err  = curl_error($ch);
    curl_close($ch);

    if (defined('FCM_LOG_PATH')) {
        $line = sprintf(
            "[%s] http=%s err=%s resp=%s\n",
            date('Y-m-d H:i:s'),
            $http,
            $err ?: '-',
            $resp ?: '-'
        );
        @file_put_contents(FCM_LOG_PATH, $line, FILE_APPEND);
    }

    if ($resp === false) {
        throw new Exception("FCM send failed: $err");
    }
    $json = json_decode($resp, true);
    if ($http !== 200) {
        throw new Exception("FCM error ($http): $resp");
    }
    return $json; // zwykle zawiera "name": "projects/.../messages/..."
}
