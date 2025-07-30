package com.partner.taxi

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private const val PREFS_NAME = "app_prefs"
    private const val DRIVER_ID_KEY = "driver_id"
    private const val TOKEN_KEY = "token"
    private const val CURRENT_SESSION_KEY = "current_session_id"
    private const val SESSION_ID_KEY = "session_id"
    private const val VEHICLE_PLATE_KEY = "vehicle_plate"

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

    fun saveCurrentSessionId(context: Context, sessionId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(CURRENT_SESSION_KEY, sessionId).apply()
    }

    fun saveSessionId(context: Context, sessionId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(SESSION_ID_KEY, sessionId).apply()
    }

    fun getCurrentSessionId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_SESSION_KEY, null)
    }

    fun getSessionId(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SESSION_ID_KEY, null)
    }

    fun clearCurrentSessionId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(CURRENT_SESSION_KEY).apply()
    }


    fun clearSessionId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(SESSION_ID_KEY).apply()
    }


    fun saveVehiclePlate(context: Context, plate: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(VEHICLE_PLATE_KEY, plate).apply()
    }

    fun getVehiclePlate(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(VEHICLE_PLATE_KEY, null)
    }

    fun clearVehiclePlate(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(VEHICLE_PLATE_KEY).apply()
    }
}
