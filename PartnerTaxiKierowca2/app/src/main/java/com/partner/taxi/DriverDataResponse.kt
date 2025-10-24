package com.partner.taxi

import com.google.gson.annotations.SerializedName

data class DriverDataResponse(
    val status: String,
    val data: DriverData?
)

data class DriverData(
    val id: String,
    val imie: String,
    val nazwisko: String,
    val saldo: Float,
    @SerializedName("voucher_current_amount") val voucherCurrentAmount: Float,
    @SerializedName("voucher_current_month") val voucherCurrentMonth: String?,
    @SerializedName("voucher_previous_amount") val voucherPreviousAmount: Float,
    @SerializedName("voucher_previous_month") val voucherPreviousMonth: String?
)
