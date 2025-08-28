package com.partner.taxi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceItem(
    val id: Int,
    val rejestracja: String,
    val opis: String,
    val koszt: Float,
    val zdjecia: List<String>,
    val data: String
) : Parcelable

data class ServicesResponse(
    val status: String,
    val services: List<ServiceItem>

)