package com.partner.taxi

import com.google.gson.annotations.SerializedName

/**
 * Response for start_shift.php containing created session ID.
 */
data class StartShiftResponse(
    val status: String,
    val message: String,
    @SerializedName("session_id") val sessionId: String?,
    @SerializedName("require_inventory") val requireInventory: Boolean?
)