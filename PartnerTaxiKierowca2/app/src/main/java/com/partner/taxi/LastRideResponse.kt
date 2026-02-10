package com.partner.taxi

data class LastRideResponse(
    val status: String,
    val data: LastRideData?,
    val message: String?
)

data class LastRideData(
    val id: Int,
    val date: String,
    val source: String,
    val type: String,
    val amount: String,
    val via_km: Int
)