<?php
/**
 * Utility loader for PHPMailer.
 *
 * Attempts to load PHPMailer either from Composer's autoloader or from the
 * legacy local copy (if it exists). Returns true when the class is available
 * so callers can degrade gracefully when the library is missing.
 */
function loadMailer(): bool
{
    static $loaded = null;
    if ($loaded !== null) {
        return $loaded;
    }

    $class = 'PHPMailer\\PHPMailer\\PHPMailer';

    if (class_exists($class)) {
        $loaded = true;
        return true;
    }

    $autoload = __DIR__ . '/vendor/autoload.php';
    if (file_exists($autoload)) {
        require_once $autoload;
        if (class_exists($class)) {
            $loaded = true;
            return true;
        }
    }

    $legacyDir = __DIR__ . '/phpmailer/PHPMailer-master/src/';
    $legacyFiles = ['Exception.php', 'PHPMailer.php', 'SMTP.php'];
    $missing = false;

    foreach ($legacyFiles as $file) {
        $path = $legacyDir . $file;
        if (!file_exists($path)) {
            $missing = true;
            break;
        }
    }

    if (!$missing) {
        foreach ($legacyFiles as $file) {
            require_once $legacyDir . $file;
        }
        if (class_exists($class)) {
            $loaded = true;
            return true;
        }
    }

    $loaded = false;
    return false;
}
