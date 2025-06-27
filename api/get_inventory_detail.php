<?php
// get_inventory_detail.php
require_once __DIR__ . '/db.php';
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'GET' || !isset($_GET['id'])) {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Brakuje parametru id']);
    exit;
}

$id = (int)$_GET['id'];

try {
    $stmt = $pdo->prepare("
        SELECT *
        FROM inwentaryzacje
        WHERE id = :id
        LIMIT 1
    ");
    $stmt->execute([':id' => $id]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$row) {
        echo json_encode(['status'=>'error','message'=>'Rekord nie znaleziony']);
        exit;
    }

    echo json_encode([
        'status' => 'success',
        'data'   => $row
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status'=>'error','message'=>'Błąd bazy danych']);
}
