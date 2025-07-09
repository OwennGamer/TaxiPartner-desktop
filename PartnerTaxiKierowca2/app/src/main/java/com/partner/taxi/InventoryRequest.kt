package com.partner.taxi

data class InventoryRequest(
    val vehicle_id: Int,
    val driver_id: String,
    val clean_inside: Boolean,
    val dirty_photo: String?, // base64 lub URL — na razie null jeśli nie ma
    val items: Map<String, Boolean>,
    val timestamp: String // np. "2025-04-08T14:32:00"
)
