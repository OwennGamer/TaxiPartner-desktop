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
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val jwt = SessionManager.getToken(applicationContext)
        if (jwt.isEmpty()) {
            Log.w("TaxiFirebaseService", "JWT token missing; skipping update")
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
                response.close()
            }
        })
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val text = message.notification?.body ?: message.data["message"] ?: return

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val last = prefs.getString(PREF_LAST_MESSAGE, null)
        if (text == last) return
        prefs.edit().putString(PREF_LAST_MESSAGE, text).apply()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Taxi notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

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