package com.partner.taxi

import java.io.Serializable

data class DamageItem(
    val id: Int,
    val rejestracja: String,
    val nr_szkody: String,
    val opis: String,
    val status: String,
    val zdjecia: List<String>,
    val data: String
) : Serializable

data class DamagesResponse(
    val status: String,
    val damages: List<DamageItem>
)