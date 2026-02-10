# API Endpoints

This directory contains PHP scripts that form the backend API.

## Centralised application logs

The desktop and mobile applications can stream crash reports and handled
exceptions to the backend using the `log_error.php` endpoint. Each log entry
stores the driver identifier, licence plate, application version and raw
stack trace, making it easy to search for incidents reported by drivers.

### Implementacja po stronie aplikacji mobilnej (Android)

Kod Androida zawiera usługę `RemoteLogService`, która automatycznie:

- rejestruje globalny `UncaughtExceptionHandler`, wysyłając crash do backendu,
- wysyła ostrzeżenia dla błędów sieciowych poprzez `showConnectionIssueToast`,
- dołącza do zgłoszenia `driver_id` oraz aktualną tablicę z `SessionManager`.

Aby raportowanie działało:

1. `App.onCreate()` wywołuje `RemoteLogService.install(this)` – mechanizm jest już dodany.
2. `ApiClient` przekazuje `Device-Id` i token JWT (jeśli dostępny), więc serwer powiąże log z kontem.
3. Własne logi można wysłać poprzez `RemoteLogService.logHandledException(...)`, `logWarning(...)` itd.,
   np. w krytycznych fragmentach kodu działalności (Activity).

Serwer przechowuje dane maksymalnie 60 dni, a logi można filtrować po `driver_id`
i `license_plate`, dzięki czemu obsługa ma ten sam wgląd w problemy z Androida i desktopu.

### `log_error.php`
- Method: `POST`
- Headers: `Device-Id` (required), `Authorization` (optional JWT)
- Body:
  ```json
  {
    "summary": "Short message shown in the UI",
    "message": "Detailed description",
    "stacktrace": "Raw stack trace (optional)",
    "driver_id": "123",
    "license_plate": "WX 12345",
    "app_version": "2.4.1",
    "source": "desktop|mobile",
    "metadata": { "any": "structured data" }
  }
  ```
- Response: `201` on success with `{ "status": "success", "log_id": number }`

### `get_error_logs.php`
- Method: `GET`
- Requires a valid admin/flotowiec JWT (includes `auth.php`).
- Query parameters (all optional):
  - `driver_id`
  - `license_plate`
  - `from` / `to` (ISO 8601, `YYYY-MM-DD HH:MM:SS` or `YYYY-MM-DD`)
  - `limit` (default 200, max 2000)
- Response: `200` with `data` array sorted by `created_at DESC`.

### `cleanup_error_logs.php`

CLI utility that deletes log entries older than 60 days. Add it to cron, e.g.:

```
0 2 * * * /usr/bin/php /var/www/api/cleanup_error_logs.php >> /var/log/cron.log 2>&1
```

## Awaria mobilna: „Problem z połączeniem z serwerem. Twoja sesja nadal jest aktywna”

Jeśli kierowca widzi wyzerowane wartości (`0,00 zł`) i toast z błędem połączenia,
najczęściej oznacza to utknięty stan sesji po stronie urządzenia i/lub serwera.

### Szybka naprawa (support / admin)

1. W panelu desktop kliknij **Wyloguj** przy kierowcy (zdalny logout przez `remote_logout.php`).
2. Poproś kierowcę o pełne zamknięcie aplikacji i ponowne logowanie.
3. Gdy problem trwa (np. brak dostępu do panelu), uruchom awaryjny reset sesji po SSH:

```bash
php api/reset_driver_session_cli.php <DRIVER_ID> [DEVICE_ID]
```

Przykład dla kierowcy `T2`:

```bash
php api/reset_driver_session_cli.php T2
```

Skrypt czyści tokeny JWT (`jwt_tokens`), sesje pomocnicze (`driver_sessions` / `sessions`,
jeśli istnieją) i `fcm_token` kierowcy.

### Jak zapobiegać

- Monitoruj wpisy z `summary = "Problem z połączeniem z API"` w `app_error_logs`
  (to zwykle `TokenRefreshException` po stronie Androida).
- Utrzymuj stabilność sieci API (reverse proxy, timeouty upstream, DNS).
- Zachowaj procedurę awaryjną: przy pierwszym zgłoszeniu „sesja nadal aktywna”
  wykonaj zdalne wylogowanie lub CLI reset zanim użytkownik zacznie reinstalację aplikacji.
- Wsparcie powinno zapisywać `driver_id`, `device_id`, wersję aplikacji i godzinę incydentu,
  aby szybciej znaleźć wpisy w `get_error_logs.php`.

## Firebase configuration

The API uses Firebase Cloud Messaging via service account credentials. Before
deploying, set the following environment variables:

- `GOOGLE_APPLICATION_CREDENTIALS` – path to the Firebase service account JSON
  file.
- `FIREBASE_PROJECT_ID` – the Firebase project identifier.

Both variables must be defined in the environment. If any of them are missing,
`config.php` returns a `500` response with a JSON error message.

The previous `FCM_SERVER_KEY` constant has been removed.

## `get_driver_stats.php`
Returns aggregated statistics for a driver between two dates.

### Response (success)
```
{
  "status": "success",
  "data": {
    "voucher": float,
    "card": float,
    "cash": float,
    "lot": float,
    "turnover": float,
    "kilometers": float,
    "fuel_sum": float,
    "missing_mileage": boolean
  }
}
```

If fewer than two mileage entries exist for the selected period,
`kilometers` sums distances of finished work sessions. If no
completed sessions exist in the range, `kilometers` will be `0`
and `missing_mileage` will be `true`.


## `start_shift.php`
Creates a new work session for the authenticated driver.

### Parameters
- `vehicle_plate` – registration number of the vehicle.
- `start_odometer` – odometer value at the start of the shift.

### Response
```
{ "status": "success", "session_id": int }
```

## `end_shift.php`
Closes the latest open session for the authenticated driver.

### Parameters
- `end_odometer` – odometer value at the end of the shift.

### Response
```
{ "status": "success", "session_id": int }
```
## `get_driver_work.php`
Returns daily working time and mileage for a driver.

### Parameters
- `driver_id` – ID of the driver.
- `start_date` – start of the range in `YYYY-MM-DD` format.
- `end_date` – end of the range in `YYYY-MM-DD` format.

### Response
```
{
  "status": "success",
  "data": [
    { "date": "YYYY-MM-DD", "hours": float, "kilometers": int },
    ...
  ]
}
```
The entries are sorted by `date`.


## `add_service.php`
Adds a vehicle service entry with optional photo uploads.

### Parameters
- `rejestracja` – vehicle plate number.
- `opis` – service description.
- `koszt` – service cost (float).
- `photos[]` – optional array of JPEG images.

### Response
```
{ "status": "success", "id": int }
```

Images are stored in `uploads/service/` and a valid JWT token is required.

## `get_services.php`
Returns service records for a vehicle.

### Parameters
- `rejestracja` – vehicle plate number.

### Response
```
{
  "status": "success",
  "services": [
    { "id": int, "rejestracja": string, "opis": string, "koszt": float, "zdjecia": [string], "data": string },
    ...
  ]
}
```

## `add_damage.php`
Registers a damage report with photo documentation.

### Parameters
- `rejestracja` – vehicle plate number.
- `nr_szkody` – damage identifier.
- `opis` – description of the damage.
- `status` – current status string.
- `photos[]` – optional array of JPEG images.

### Response
```
{ "status": "success", "id": int }
```

Images are stored in `uploads/damages/` and a valid JWT token is required.

## `get_damages.php`
Lists damages reported for a vehicle.

### Parameters
- `rejestracja` – vehicle plate number.

### Response
```
{
  "status": "success",
  "damages": [
    { "id": int, "rejestracja": string, "nr_szkody": string, "opis": string, "status": string, "zdjecia": [string], "data": string },
    ...
  ]
}
```

All endpoints in this section require an `Authorization: Bearer <JWT>` header.

## `remote_logout.php`
Remotely logs out a driver. Requires an admin JWT.

### Request
POST JSON body:
```
{ "id": "driver_id" }
```

### Response
```
{ "status": "success" }
```
Returns an error message on failure.
