<?php
require_once 'db.php'; // tutaj masz $pdo
header('Content-Type: application/json');

file_put_contents("debug_log.txt", "[" . date('Y-m-d H:i:s') . "] Endpoint wywołany\n", FILE_APPEND);

if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['rejestracja'])) {
    $rejestracja = $_GET['rejestracja'];
    file_put_contents("debug_log.txt", "Rejestracja: $rejestracja\n", FILE_APPEND);

    try {
        $stmt = $pdo->prepare("SELECT rejestracja, przebieg, ostatni_kierowca_id FROM pojazdy WHERE LOWER(rejestracja) = LOWER(:rej)");
        $stmt->bindParam(':rej', $rejestracja, PDO::PARAM_STR);
        $stmt->execute();

        if ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            file_put_contents("debug_log.txt", "Pojazd znaleziony: " . json_encode($row) . "\n", FILE_APPEND);
            echo json_encode([
                'status' => 'success',
                'data' => [
                    'rejestracja' => $row['rejestracja'],
                    'przebieg' => (int)$row['przebieg'],
                    'ostatni_kierowca_id' => $row['ostatni_kierowca_id']
                ]
            ]);
        } else {
            file_put_contents("debug_log.txt", "Brak wyników\n", FILE_APPEND);
            echo json_encode(['status' => 'error', 'message' => 'Pojazd nie znaleziony']);
        }
    } catch (PDOException $e) {
        file_put_contents("debug_log.txt", "Błąd PDO: " . $e->getMessage() . "\n", FILE_APPEND);
        echo json_encode(['status' => 'error', 'message' => 'Błąd zapytania']);
    }
} else {
    file_put_contents("debug_log.txt", "Nieprawidłowe żądanie\n", FILE_APPEND);
    echo json_encode(['status' => 'error', 'message' => 'Nieprawidłowe żądanie']);
}
