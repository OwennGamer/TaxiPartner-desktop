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
import java.util.Locale
import kotlin.math.abs

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
                val locale = Locale("pl", "PL")
                val saldoChange = parseAmount(message.data["saldo_change"]) ?: 0.0
                val saldoAfter = parseAmount(message.data["saldo_po"])
                val voucherCurrentChange = parseAmount(message.data["voucher_current_change"]) ?: 0.0
                val voucherCurrentAfter = parseAmount(message.data["voucher_current_after"])
                val voucherPreviousChange = parseAmount(message.data["voucher_previous_change"]) ?: 0.0
                val voucherPreviousAfter = parseAmount(message.data["voucher_previous_after"])
                val reason = message.data["reason"]?.takeIf { it.isNotBlank() }
                val initiatorRole = message.data["initiator_role"]?.takeIf { it.isNotBlank() }
                val initiatorId = message.data["initiator_id"]?.takeIf { it.isNotBlank() }

                val changeParts = mutableListOf<String>()
                if (abs(saldoChange) > 1e-6) {
                    val part = buildChangeSummary(
                        getString(R.string.change_saldo_notification_saldo_label),
                        saldoChange,
                        saldoAfter,
                        locale
                    )
                    changeParts += part
                }
                if (abs(voucherCurrentChange) > 1e-6) {
                    val part = buildChangeSummary(
                        getString(R.string.change_saldo_notification_voucher_current_label),
                        voucherCurrentChange,
                        voucherCurrentAfter,
                        locale
                    )
                    changeParts += part
                }
                if (abs(voucherPreviousChange) > 1e-6) {
                    val part = buildChangeSummary(
                        getString(R.string.change_saldo_notification_voucher_previous_label),
                        voucherPreviousChange,
                        voucherPreviousAfter,
                        locale
                    )
                    changeParts += part
                }

                val summary = if (changeParts.isNotEmpty()) {
                    getString(
                        R.string.change_saldo_notification_summary,
                        changeParts.joinToString(", ")
                    )
                } else null

                val notificationBody = buildString {
                    if (!summary.isNullOrBlank()) {
                        append(summary)
                    }
                    if (!reason.isNullOrBlank()) {
                        if (isNotEmpty()) append(". ")
                        append(getString(R.string.change_saldo_notification_reason, reason))
                    }
                    if (!initiatorRole.isNullOrBlank() || !initiatorId.isNullOrBlank()) {
                        if (isNotEmpty()) append(". ")
                        val who = listOfNotNull(initiatorRole, initiatorId).joinToString(" ")
                        append(getString(R.string.change_saldo_notification_initiator, who))
                    }
                }

                if (notificationBody.isNotEmpty()) {

                    val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val last = prefs.getString(PREF_LAST_MESSAGE, null)
                    if (notificationBody != last) {
                        prefs.edit().putString(PREF_LAST_MESSAGE, notificationBody).apply()
                        message.data["saldo_po"]?.let {
                            prefs.edit().putString("last_saldo", it).apply()
                        }
                        message.data["voucher_current_after"]?.let {
                            prefs.edit().putString("last_voucher_current", it).apply()
                        }
                        message.data["voucher_previous_after"]?.let {
                            prefs.edit().putString("last_voucher_previous", it).apply()
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
                            .setContentTitle(getString(R.string.change_saldo_notification_title))
                            .setContentText(notificationBody)
                            .setStyle(
                                NotificationCompat.BigTextStyle().bigText(notificationBody)
                            )
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

    private fun parseAmount(raw: String?): Double? {
        if (raw.isNullOrBlank()) return null
        return raw.replace(',', '.').toDoubleOrNull()
    }

    private fun buildChangeSummary(
        label: String,
        change: Double,
        after: Double?,
        locale: Locale
    ): String {
        val changeText = String.format(locale, "%+.2f zł", change)
        val afterText = after?.let { String.format(locale, "%.2f zł", it) }
        return if (afterText != null) {
            "$label $changeText (po: $afterText)"
        } else {
            "$label $changeText"
        }
    }

}