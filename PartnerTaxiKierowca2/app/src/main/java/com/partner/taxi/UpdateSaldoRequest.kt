package com.partner.taxi

import com.google.gson.annotations.SerializedName

data class UpdateSaldoRequest(
    val id: String,
    @SerializedName("saldo_amount") val saldoAmount: Double,
    @SerializedName("voucher_current_amount") val voucherCurrentAmount: Double,
    @SerializedName("voucher_previous_amount") val voucherPreviousAmount: Double,
    val reason: String,
    @SerializedName("custom_reason") val customReason: String? = null
)