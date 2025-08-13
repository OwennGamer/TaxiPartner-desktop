# API Endpoints

This directory contains PHP scripts that form the backend API.

## Firebase configuration

The API uses Firebase Cloud Messaging via service account credentials. Before
deploying, set the following environment variables:

- `GOOGLE_APPLICATION_CREDENTIALS` – path to the Firebase service account JSON
  file.
- `FIREBASE_PROJECT_ID` – the Firebase project identifier.

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
