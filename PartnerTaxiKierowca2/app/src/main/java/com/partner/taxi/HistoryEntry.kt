package com.partner.taxi

import com.google.gson.annotations.SerializedName

data class HistoryEntry(
    @SerializedName("date")
    val dateTime: String,        // JSON-owe pole "date"

    @SerializedName("source")
    val source: String,          // JSON-owe pole "source"

    @SerializedName("type")
    val paymentType: String,     // teraz poprawnie mapujemy JSON-owe "type"

    @SerializedName("amount")
    val amount: String           // JSON-owe pole "amount"
)
