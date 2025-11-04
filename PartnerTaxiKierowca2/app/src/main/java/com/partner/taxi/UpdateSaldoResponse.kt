package com.partner.taxi

import com.google.gson.annotations.SerializedName

data class UpdateSaldoResponse(
    val status: String,
    val message: String?,
    @SerializedName("driver_id") val driverId: String?,
    @SerializedName("new_saldo") val newSaldo: Double?,
    @SerializedName("saldo_change") val saldoChange: Double?,
    @SerializedName("voucher_current_amount") val voucherCurrentAmount: Double?,
    @SerializedName("voucher_current_change") val voucherCurrentChange: Double?,
    @SerializedName("voucher_current_after") val voucherCurrentAfter: Double?,
    @SerializedName("voucher_previous_amount") val voucherPreviousAmount: Double?,
    @SerializedName("voucher_previous_change") val voucherPreviousChange: Double?,
    @SerializedName("voucher_previous_after") val voucherPreviousAfter: Double?,
    val reason: String?,
    @SerializedName("requested_by") val requestedBy: UpdateSaldoActor?,
    @SerializedName("fcm_status") val fcmStatus: String?
)

data class UpdateSaldoActor(
    @SerializedName("user_id") val userId: String?,
    val role: String?
)