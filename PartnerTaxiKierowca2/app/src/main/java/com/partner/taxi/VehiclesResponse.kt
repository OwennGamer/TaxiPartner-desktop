package com.partner.taxi

data class VehiclesResponse(
    val status: String,
    val data: List<VehicleData>
)