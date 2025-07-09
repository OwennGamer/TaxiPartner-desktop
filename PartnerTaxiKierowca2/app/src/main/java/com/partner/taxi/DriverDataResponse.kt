package com.partner.taxi

data class DriverDataResponse(
    val status: String,
    val data: DriverData?
)

data class DriverData(
    val id: String,
    val imie: String,
    val nazwisko: String,
    val saldo: Float
)
