package com.partner.taxi

data class HistoryResponse(
    val status: String,
    val data: List<HistoryEntry>
)
