package com.partner.taxi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.util.Base64
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        if (BuildConfig.DEBUG) {
            try {
                val payload = jwt.split(".").getOrNull(1)
                if (!payload.isNullOrEmpty()) {
                    val decoded = String(
                        Base64.decode(
                            payload,
                            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
                        )
                    )
                    val obj = JSONObject(decoded)
                    val role = obj.optString("role")
                    val deviceId = obj.optString("device_id")
                    Log.d(
                        "TaxiFirebaseService",
                        "JWT role=$role, device_id=$deviceId"
                    )
                }
            } catch (e: Exception) {
                Log.d("TaxiFirebaseService", "Failed to decode JWT", e)
            }
        }

        // Ensure ApiClient has the current auth data
        ApiClient.jwtToken = jwt
        ApiClient.deviceId = SessionManager.getDeviceId(applicationContext)

        ApiClient.apiService.updateFcmToken(token).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    prefs.edit().remove(PREF_PENDING_FCM_TOKEN).apply()
                } else {
                    Log.e(
                        "TaxiFirebaseService",
                        "Failed to update token: ${response.body()?.message}"
                    )
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Log.e("TaxiFirebaseService", "Failed to send token", t)
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
                ApiClient.jwtToken = null
                ApiClient.deviceId = null

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