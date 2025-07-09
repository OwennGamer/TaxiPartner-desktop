package com.partner.taxi

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREFS_NAME = "app_prefs"
    private const val DRIVER_ID_KEY = "driver_id"
    private const val TOKEN_KEY = "token"

    fun saveDriverId(context: Context, driverId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(DRIVER_ID_KEY, driverId).apply()
    }

    fun getDriverId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(DRIVER_ID_KEY, "") ?: ""
    }

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(TOKEN_KEY, "") ?: ""
    }

    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
