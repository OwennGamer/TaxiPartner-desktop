#!/usr/bin/env php
<?php
require_once __DIR__ . '/error_log_helpers.php';

if (PHP_SAPI !== 'cli') {
    fwrite(STDERR, "This utility can only be executed from the command line.\n");
    exit(1);
}

$options = getopt(
    '',
    [
        'driver:',
        'device:',
        'source:',
        'level:',
        'from:',
        'to:',
        'contains:',
        'limit:',
        'json',
        'help',
    ]
);

if ($options === false || isset($options['help'])) {
    echo "Usage: php read_error_log.php [options]\n";
    echo "\n";
    echo "Options:\n";
    echo "  --driver=ID        Filter by driver_id.\n";
    echo "  --device=DEVICE    Filter by Device-Id header.\n";
    echo "  --source=NAME      Filter by source field (mobile, desktop, itp.).\n";
    echo "  --level=LEVEL      Filter by level (DEBUG|INFO|WARN|ERROR|FATAL).\n";
    echo "  --from=DATE        ISO8601 lub 'YYYY-MM-DD HH:MM'. Porównuje reported_at.\n";
    echo "  --to=DATE          Jak wyżej, filtruje reported_at.\n";
    echo "  --contains=TEXT    Szuka tekstu w summary/message/stacktrace.\n";
    echo "  --limit=N          Maksimum wyników (domyślnie 20, 0 = bez limitu).\n";
    echo "  --json             Zwracaj dopasowania jako linie JSON.\n";
    echo "  --help             Wyświetl tę pomoc.\n";
    exit(isset($options['help']) ? 0 : 1);
}

if (!is_file(ERROR_LOG_FILE)) {
    fwrite(STDERR, "Brak pliku logów: " . ERROR_LOG_FILE . "\n");
    fwrite(STDERR, "Zgłoszenia pojawią się tutaj po pierwszym błędzie zapisanym przez log_error.php.\n");
    exit(1);
}

$limitOption = isset($options['limit']) ? trim($options['limit']) : '20';
$limit = null;
if ($limitOption !== '' && $limitOption !== '0') {
    if (!ctype_digit($limitOption)) {
        fwrite(STDERR, "--limit musi być liczbą całkowitą >= 0\n");
        exit(1);
    }
    $limit = (int) $limitOption;
    if ($limit === 0) {
        $limit = null;
    }
}

$driverFilter = isset($options['driver']) ? trim($options['driver']) : null;
$deviceFilter = isset($options['device']) ? trim($options['device']) : null;
$sourceFilter = isset($options['source']) ? strtolower(trim($options['source'])) : null;
$levelFilter = isset($options['level']) ? strtoupper(trim($options['level'])) : null;
$textFilter = isset($options['contains']) ? mb_strtolower(trim($options['contains'])) : null;

$from = isset($options['from']) ? parseDateBoundary($options['from'], '--from') : null;
$to = isset($options['to']) ? parseDateBoundary($options['to'], '--to') : null;

$matches = [];

$handle = fopen(ERROR_LOG_FILE, 'rb');
if ($handle === false) {
    fwrite(STDERR, "Nie udało się otworzyć pliku logu." . PHP_EOL);
    exit(1);
}

while (($line = fgets($handle)) !== false) {
    $line = trim($line);
    if ($line === '') {
        continue;
    }

    $entry = json_decode($line, true);
    if (!is_array($entry)) {
        continue;
    }

    if ($driverFilter !== null && (string) ($entry['driver_id'] ?? '') !== $driverFilter) {
        continue;
    }
    if ($deviceFilter !== null && (string) ($entry['device_id'] ?? '') !== $deviceFilter) {
        continue;
    }
    if ($sourceFilter !== null && strtolower((string) ($entry['source'] ?? '')) !== $sourceFilter) {
        continue;
    }
    if ($levelFilter !== null && strtoupper((string) ($entry['level'] ?? '')) !== $levelFilter) {
        continue;
    }
    if ($textFilter !== null) {
        $haystack = mb_strtolower(
            implode(' ', array_filter([
                (string) ($entry['summary'] ?? ''),
                (string) ($entry['message'] ?? ''),
                (string) ($entry['stacktrace'] ?? ''),
            ]))
        );
        if ($haystack === '' || mb_strpos($haystack, $textFilter) === false) {
            continue;
        }
    }

    $reportedAt = isset($entry['reported_at']) ? parseDateTime($entry['reported_at']) : null;
    if ($from !== null && ($reportedAt === null || $reportedAt < $from)) {
        continue;
    }
    if ($to !== null && ($reportedAt === null || $reportedAt > $to)) {
        continue;
    }

    $matches[] = $entry;
    if ($limit !== null && count($matches) > $limit) {
        array_shift($matches);
    }
}

fclose($handle);

if (empty($matches)) {
    fwrite(STDERR, "Nie znaleziono żadnych logów pasujących do filtrów.\n");
    exit(1);
}

if (isset($options['json'])) {
    foreach ($matches as $entry) {
        echo json_encode($entry, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . PHP_EOL;
    }
    exit(0);
}

foreach ($matches as $entry) {
    echo str_repeat('=', 80) . PHP_EOL;
    echo 'log_id:      ' . ($entry['log_id'] ?? 'n/d') . PHP_EOL;
    echo 'reported_at: ' . ($entry['reported_at'] ?? 'n/d') . PHP_EOL;
    echo 'occurred_at: ' . ($entry['occurred_at'] ?? 'n/d') . PHP_EOL;
    echo 'level:       ' . ($entry['level'] ?? 'n/d') . PHP_EOL;
    echo 'source:      ' . ($entry['source'] ?? 'n/d') . PHP_EOL;
    echo 'device_id:   ' . ($entry['device_id'] ?? 'n/d') . PHP_EOL;
    echo 'driver_id:   ' . ($entry['driver_id'] ?? 'n/d') . PHP_EOL;
    echo 'license:     ' . ($entry['license_plate'] ?? 'n/d') . PHP_EOL;
    echo 'app_version: ' . ($entry['app_version'] ?? 'n/d') . PHP_EOL;
    echo 'summary:     ' . ($entry['summary'] ?? '') . PHP_EOL;
    if (!empty($entry['message'])) {
        echo PHP_EOL . "-- message --" . PHP_EOL;
        echo (string) $entry['message'] . PHP_EOL;
    }
    if (!empty($entry['stacktrace'])) {
        echo PHP_EOL . "-- stacktrace --" . PHP_EOL;
        echo (string) $entry['stacktrace'] . PHP_EOL;
    }
    if (isset($entry['metadata']) && $entry['metadata'] !== null) {
        echo PHP_EOL . "-- metadata --" . PHP_EOL;
        echo json_encode($entry['metadata'], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES) . PHP_EOL;
    }
    echo PHP_EOL;
}

exit(0);

/**
 * @return DateTimeImmutable|null
 */
function parseDateTime(string $value)
{
    try {
        return new DateTimeImmutable($value);
    } catch (Exception $e) {
        return null;
    }
}

/**
 * @return DateTimeImmutable|null
 */
function parseDateBoundary(string $value, string $option)
{
    $dt = parseDateTime($value);
    if ($dt === null) {
        fwrite(STDERR, sprintf("Nie można sparsować wartości %s: %s\n", $option, $value));
        exit(1);
    }
    return $dt;
}





