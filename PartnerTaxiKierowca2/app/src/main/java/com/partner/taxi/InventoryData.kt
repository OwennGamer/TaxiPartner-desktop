package com.partner.taxi

data class InventoryData(
    val rejestracja: String,
    val przebieg: Int,
    val isClean: Boolean,
    val licencja: Boolean,
    val legalizacja: Boolean,
    val dowod: Boolean,
    val ubezpieczenie: Boolean,
    val kartaLotniskowa: Boolean,
    val gasnica: Boolean,
    val lewarek: Boolean,
    val trojkat: Boolean,
    val kamizelka: Boolean
)
