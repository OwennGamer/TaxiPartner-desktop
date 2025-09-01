package com.partner.taxi  // ← jeśli masz inny pakiet, wpisz go tutaj

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
        // Przywróć token z pamięci i ustaw w ApiClient
        val token = SessionManager.getToken(this)
        if (token.isNotEmpty()) {
            ApiClient.jwtToken = token
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",                // ID kanału – musi być "default"
                "Powiadomienia",          // nazwa kanału widoczna w systemie
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Ogólne powiadomienia aplikacji"
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }
}
