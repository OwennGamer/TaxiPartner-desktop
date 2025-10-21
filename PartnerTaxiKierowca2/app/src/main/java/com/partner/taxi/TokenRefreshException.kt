package com.partner.taxi

import java.io.IOException

class TokenRefreshException(
    message: String,
    cause: IOException? = null
) : IOException(message, cause)