<?php
// get_inventory_history.php
require_once __DIR__ . '/db.php';
header('Content-Type: application/json; charset=utf-8');

// Upewnij się, że to GET i jest parametr rejestracja
if ($_SERVER['REQUEST_METHOD'] !== 'GET' || !isset($_GET['rejestracja'])) {
    http_response_code(400);
    echo json_encode(['status'=>'error','message'=>'Brakuje parametru rejestracja']);
    exit;
}

$rejestracja = trim($_GET['rejestracja']);

try {
    // pobieramy ID, datę i przebieg
    $stmt = $pdo->prepare("
    SELECT 
      id,
      data_dodania AS datetime,
      przebieg,
      kierowca_id
    FROM inwentaryzacje
    WHERE LOWER(rejestracja) = LOWER(:rej)
    ORDER BY data_dodania DESC
");

    $stmt->execute([':rej' => $rejestracja]);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data'   => $rows
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['status'=>'error','message'=>'Błąd bazy danych']);
}
