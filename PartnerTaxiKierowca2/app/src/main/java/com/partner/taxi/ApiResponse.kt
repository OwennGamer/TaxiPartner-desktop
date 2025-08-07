package com.partner.taxi

data class ApiResponse(
    val status: String,
    val message: String,
    val id: Int? = null
)
