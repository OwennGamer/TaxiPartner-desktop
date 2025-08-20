package com.partner.taxi

data class VehicleResponse(
    val status: String,
    val data: VehicleData?
)

data class VehicleData(
    val id: Int,
    val rejestracja: String,
    val marka: String?,
    val model: String?,
    val przebieg: Int,
    val ubezpieczenie_do: String?,
    val przeglad_do: String?,
    val aktywny: Boolean,
    val ostatni_kierowca_id: String?,
    val inpost: Boolean,
    val taxi: Boolean,
    val taksometr: Boolean,
    val legalizacja_taksometru_do: String?,
    val gaz: Boolean,
    val homologacja_lpg_do: String?,
    val firma: String?,
    val forma_wlasnosci: String?,
    val numer_polisy: String?
) : java.io.Serializable
