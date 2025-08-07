package com.partner.taxi

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
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
}