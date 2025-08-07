<?php
// db.php – połączenie z bazą przez PDO

require_once 'config.php';

try {
    // składamy DSN z wykorzystaniem portu z config.php
    $dsn = sprintf(
        "mysql:host=%s;port=%s;dbname=%s;charset=utf8mb4",
        DB_HOST,
        DB_PORT,
        DB_NAME
    );

    $pdo = new PDO($dsn, DB_USER, DB_PASS, [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    error_log("DB CONNECTION ERROR: " . $e->getMessage());
    echo json_encode([
        "status"  => "error",
        "message" => "Błąd połączenia z bazą"
    ]);
    exit;
}
