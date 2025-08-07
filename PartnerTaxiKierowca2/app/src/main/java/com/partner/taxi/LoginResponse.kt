package com.partner.taxi

data class LoginResponse(
    val status: String,
    val message: String,
    val token: String?,
    val driver_id: String?
)

