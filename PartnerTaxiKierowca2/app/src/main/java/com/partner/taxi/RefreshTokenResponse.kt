package com.partner.taxi

data class RefreshTokenResponse(
    val status: String,
    val token: String?
)