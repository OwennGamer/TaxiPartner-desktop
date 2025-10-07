package com.partner.taxi

import com.google.gson.annotations.SerializedName

data class MobileUpdateResponse(
    val status: String?,
    val message: String?,
    val data: MobileUpdateData?
)

data class MobileUpdateData(
    val version: String?,
    val url: String?,
    val changelog: String?,
    @SerializedName("mandatory")
    val mandatory: Boolean? = false
)