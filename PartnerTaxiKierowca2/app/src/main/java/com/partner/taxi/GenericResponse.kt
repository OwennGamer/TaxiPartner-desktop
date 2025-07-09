package com.partner.taxi

/**
 * Ogólna odpowiedź serwera dla operacji typu update, delete, add itp.
 */
data class GenericResponse(
    val status: String,   // np. "success" lub "error"
    val message: String   // komunikat zwrócony przez API
)
