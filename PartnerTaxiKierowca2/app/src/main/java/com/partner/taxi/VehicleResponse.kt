package com.partner.taxi

data class VehicleResponse(
    val status: String,
    val data: VehicleData?
)

data class VehicleData(
    val id: Int,
    val rejestracja: String,
    val przebieg: Int,
    val ostatni_kierowca_id: String?
)
