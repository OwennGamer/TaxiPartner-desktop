<?php
require_once 'db.php';    // już masz $pdo
header('Content-Type: application/json; charset=utf-8');

file_put_contents("debug_log.txt", "[".date('Y-m-d H:i:s')."] update_vehicle_mileage called\n", FILE_APPEND);

if ($_SERVER['REQUEST_METHOD']==='POST') {
    $rejestracja = $_POST['rejestracja'] ?? '';
    $przebieg    = (int)($_POST['przebieg'] ?? -1);

    if (trim($rejestracja)==='' || $przebieg < 0) {
        http_response_code(400);
        echo json_encode(['status'=>'error','message'=>'Nieprawidłowe dane']);
        exit;
    }

    try {
        $stmt = $pdo->prepare(
            "UPDATE pojazdy
             SET przebieg = :prz
             WHERE LOWER(rejestracja)=LOWER(:rej)"
        );
        $stmt->bindParam(':prz', $przebieg, PDO::PARAM_INT);
        $stmt->bindParam(':rej', $rejestracja, PDO::PARAM_STR);
        $stmt->execute();

        if ($stmt->rowCount()>0) {
            echo json_encode(['status'=>'success','message'=>'Przebieg zaktualizowany']);
        } else {
            $check = $pdo->prepare(
                "SELECT id FROM pojazdy WHERE LOWER(rejestracja)=LOWER(?)"
            );
            $check->execute([$rejestracja]);
            if ($check->fetch()) {
                echo json_encode(['status'=>'success','message'=>'Przebieg bez zmian']);
            } else {
                echo json_encode(['status'=>'error','message'=>'Brak pojazdu']);
            }
        }
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode(['status'=>'error','message'=>'Błąd bazy: '.$e->getMessage()]);
    }
} else {
    http_response_code(405);
    echo json_encode(['status'=>'error','message'=>'Nieprawidłowe żądanie']);
}
