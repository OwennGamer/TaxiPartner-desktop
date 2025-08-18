<?php
header('Content-Type: application/json; charset=utf-8');
$path = getenv('GOOGLE_APPLICATION_CREDENTIALS');
echo json_encode([
  'GOOGLE_APPLICATION_CREDENTIALS' => $path,
  'exists'   => $path ? file_exists($path) : null,
  'readable' => $path ? is_readable($path) : null,
  'php_sapi' => php_sapi_name(),
], JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES);

