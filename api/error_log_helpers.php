<?php
const ERROR_LOG_DIR = __DIR__ . '/logs';
const ERROR_LOG_FILE = ERROR_LOG_DIR . '/app_error.log';

/**
 * @param array<string,mixed> $entry
 */
function appendLogEntry(array $entry): void
{
    if (!is_dir(ERROR_LOG_DIR)) {
        if (!@mkdir(ERROR_LOG_DIR, 0775, true) && !is_dir(ERROR_LOG_DIR)) {
            error_log('log_error.php: unable to create log directory ' . ERROR_LOG_DIR);
            return;
        }
    }

    $encoded = json_encode($entry, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    if ($encoded === false) {
        error_log('log_error.php: failed to encode log entry');
        return;
    }

    $result = @file_put_contents(ERROR_LOG_FILE, $encoded . PHP_EOL, FILE_APPEND | LOCK_EX);
    if ($result === false) {
        error_log('log_error.php: failed to write to log file ' . ERROR_LOG_FILE);
    }
}

