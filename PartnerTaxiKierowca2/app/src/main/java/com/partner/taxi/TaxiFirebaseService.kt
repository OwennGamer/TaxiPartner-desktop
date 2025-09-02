package com.partner.taxi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject

class TaxiFirebaseService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "taxi_notifications"
        private const val PREF_LAST_MESSAGE = "last_fcm_message"
        private const val PREF_PENDING_FCM_TOKEN = "pending_fcm_token"
    }

    private fun ensureChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Taxi notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_PENDING_FCM_TOKEN, token).apply()

        val jwt = SessionManager.getToken(applicationContext)
        if (jwt.isEmpty()) {
            Log.w("TaxiFirebaseService", "JWT token missing; stored token for later")
            return
        }

        val json = JSONObject().apply {
            put("fcm_token", token)
        }

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url("http://164.126.143.20:8444/api/update_fcm_token.php")
            .post(body)
            .addHeader("Authorization", "Bearer $jwt")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TaxiFirebaseService", "Failed to send token", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code == 401) {
                    SessionManager.clearSession(applicationContext)
                    val loginIntent = Intent(applicationContext, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(loginIntent)
                } else {
                    prefs.edit().remove(PREF_PENDING_FCM_TOKEN).apply()
                }
                response.close()
            }
        })
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        ensureChannel(notificationManager)

        when (message.data["type"]) {
            "logout" -> {
                SessionManager.clearSession(applicationContext)
                SessionManager.clearSessionId(applicationContext)

                val loginIntent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(loginIntent)

                val pending = PendingIntent.getActivity(
                    this,
                    0,
                    loginIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Taxi Partner")
                    .setContentText("Zostałeś wylogowany")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(pending)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(1002, notification)
                return
            }

            "saldo_update" -> {
                val amount = message.data["amount"]
                val saldoPo = message.data["saldo_po"]
                if (amount != null && saldoPo != null) {
                    val text = "Zmiana salda: $amount, nowe saldo: $saldoPo"

                    val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val last = prefs.getString(PREF_LAST_MESSAGE, null)
                    if (text != last) {
                        prefs.edit().putString(PREF_LAST_MESSAGE, text).apply()
                        // Opcjonalnie zapamiętaj najnowsze saldo
                        prefs.edit().putString("last_saldo", saldoPo).apply()

                        val intent = Intent(this, DashboardActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        val pending = PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

                        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle("Taxi Partner")
                            .setContentText(text)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setLargeIcon(largeIcon)
                            .setContentIntent(pending)
                            .setAutoCancel(true)
                            .build()

                        notificationManager.notify(1003, notification)
                    }
                    return
                }
            }

        }
        val text = message.notification?.body ?: message.data["message"] ?: return

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val last = prefs.getString(PREF_LAST_MESSAGE, null)
        if (text == last) return
        prefs.edit().putString(PREF_LAST_MESSAGE, text).apply()


        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Taxi Partner")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIcon)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

}