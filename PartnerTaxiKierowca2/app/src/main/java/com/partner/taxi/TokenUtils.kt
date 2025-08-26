package com.partner.taxi

import android.app.Activity
import android.content.Intent

fun Activity.ensureTokenOrRedirect(): Boolean {
    if (ApiClient.jwtToken.isNullOrEmpty()) {
        val token = SessionManager.getToken(this)
        if (token.isNotEmpty()) {
            ApiClient.jwtToken = token
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return false
        }
    }
    return true
}