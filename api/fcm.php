<?php
require_once __DIR__ . '/vendor/autoload.php';

use Google\Auth\Credentials\ServiceAccountCredentials;

/**
 * Retrieves an OAuth2 access token for Firebase Cloud Messaging.
 *
 * The path to the service account key file is taken from the
 * GOOGLE_APPLICATION_CREDENTIALS environment variable or defaults to a file
 * named "service-account.json" located in the same directory as this script.
 *
 * @return string OAuth2 access token
 * @throws RuntimeException When the token could not be fetched
 */
function getAccessToken(): string
{
    $scopes = ['https://www.googleapis.com/auth/firebase.messaging'];
    $credentialsPath = getenv('GOOGLE_APPLICATION_CREDENTIALS') ?: __DIR__ . '/service-account.json';

    $credentials = new ServiceAccountCredentials($scopes, $credentialsPath);
    $token = $credentials->fetchAuthToken();

    if (!isset($token['access_token'])) {
        throw new RuntimeException('Unable to fetch access token.');
    }

    return $token['access_token'];
}

/**
 * Sends a message via Firebase Cloud Messaging HTTP v1 API.
 *
 * @param string $projectId Google Cloud project identifier.
 * @param array  $message   Message payload conforming to the FCM API.
 *
 * @return array{statusCode:int,body:string} Response data from the FCM API.
 * @throws RuntimeException When the HTTP request fails
 */
function sendFcmMessage(string $projectId, array $message): array
{
    $url = sprintf('https://fcm.googleapis.com/v1/projects/%s/messages:send', $projectId);
    $accessToken = getAccessToken();

    $headers = [
        'Authorization: Bearer ' . $accessToken,
        'Content-Type: application/json; charset=utf-8'
    ];

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode(['message' => $message]));

    $response = curl_exec($ch);
    if ($response === false) {
        $error = curl_error($ch);
        curl_close($ch);
        throw new RuntimeException('FCM send error: ' . $error);
    }

    $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    return ['statusCode' => $status, 'body' => $response];
}
