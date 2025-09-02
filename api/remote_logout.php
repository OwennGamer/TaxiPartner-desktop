<?php
require_once __DIR__ . '/auth.php';
require_once __DIR__ . '/db.php';
require_once __DIR__ . '/fcm_v1.php';

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

if (($decoded->role ?? '') !== 'admin') {
    http_response_code(403);
    echo json_encode(['status' => 'error', 'message' => 'Brak uprawnień']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$driverId = $input['id'] ?? '';
$deviceId = $input['device_id'] ?? null;
if ($driverId === '') {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Brak ID kierowcy']);
    exit;
}

function tableExists(PDO $pdo, string $table): bool {
    $stmt = $pdo->prepare('SHOW TABLES LIKE ?');
    $stmt->execute([$table]);
    return $stmt->fetchColumn() !== false;
}

try {
    $stmt = $pdo->prepare('SELECT fcm_token FROM kierowcy WHERE id = ?');
    $stmt->execute([$driverId]);
    $fcmToken = $stmt->fetchColumn();

    $fcmStatus = 'no_token';
    if ($fcmToken) {
        try {
            $resp = sendFcmV1(
                $fcmToken,
                'Taxi Partner',
                'Zostałeś zdalnie wylogowany',
                ['type' => 'logout']
            );
            $fcmStatus = $resp === null ? 'skipped:no_credentials' : 'sent';
        } catch (Exception $e) {
            $fcmStatus = 'error: ' . $e->getMessage();
        }
    }

    $pdo->beginTransaction();

    $stmt = $pdo->prepare('UPDATE kierowcy SET fcm_token = NULL WHERE id = ?');
    $stmt->execute([$driverId]);

    $tables = ['jwt_tokens', 'driver_sessions', 'sessions'];
    foreach ($tables as $tbl) {
        if (!tableExists($pdo, $tbl)) {
            continue;
        }

        if ($tbl === 'jwt_tokens') {
            $sql = "DELETE FROM {$tbl} WHERE driver_id = ?";
            $params = [$driverId];
            if ($deviceId !== null && $deviceId !== '') {
                $sql .= " AND device_id = ?";
                $params[] = $deviceId;
            }
            $del = $pdo->prepare($sql);
            $del->execute($params);
        } else {
            $del = $pdo->prepare("DELETE FROM {$tbl} WHERE driver_id = ?");
            $del->execute([$driverId]);
        }
    }

    $pdo->commit();
    echo json_encode(['status' => 'success', 'fcm_status' => $fcmStatus]);
} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Błąd bazy danych']);
}
