# API Endpoints

This directory contains PHP scripts that form the backend API.

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
`kilometers` will be `0` and `missing_mileage` will be `true`.
