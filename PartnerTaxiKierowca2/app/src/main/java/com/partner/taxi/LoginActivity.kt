package com.partner.taxi

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val api = ApiClient.apiService
    private lateinit var deviceId: String

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val allGranted = permissions.values.all { it }
                if (allGranted) {
                    proceedAfterPermissions()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Wymagane uprawnienia")
                        .setMessage("Odmowa uprawnień do powiadomień i aparatu uniemożliwia działanie aplikacji.")
                        .setPositiveButton("Ponów") { _, _ ->
                            permissionLauncher.launch(getRequiredPermissions())
                        }
                        .setNegativeButton("Zamknij") { _, _ -> finish() }
                        .show()
                }
            }

        permissionLauncher.launch(getRequiredPermissions())
    }

    private fun proceedAfterPermissions() {
        val savedToken = SessionManager.getToken(this)
        val savedDeviceId = SessionManager.getDeviceId(this)
        if (savedToken.isNotEmpty() && savedDeviceId == deviceId && isTokenValid(savedToken, deviceId)) {
            ApiClient.jwtToken = savedToken
            ApiClient.deviceId = deviceId
            startActivity(Intent(this, ChooseVehicleActivity::class.java))
            finish()
            return
        }
        initLoginUI()
    }

    private fun initLoginUI() {

        setContentView(R.layout.activity_login)

        val loginEt = findViewById<EditText>(R.id.editUsername)
        val passwordEt = findViewById<EditText>(R.id.editPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val user = loginEt.text.toString().trim()
            val pass = passwordEt.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Wprowadź login i hasło", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            api.login(user, pass, deviceId).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.d("LOGIN", "HTTP Code: ${response.code()}")
                    Log.d("LOGIN", "Body: ${response.body()}")

                    val loginResponse = response.body()
                    if (response.isSuccessful
                        && loginResponse?.status == "success"
                        && loginResponse.token != null
                    ) {
                        // zapisujemy token i deviceId dla interceptor’a
                        ApiClient.jwtToken = loginResponse.token
                        ApiClient.deviceId = deviceId

                        // zapisujemy w SharedPreferences
                        SessionManager.saveToken(this@LoginActivity, loginResponse.token)
                        SessionManager.saveDeviceId(this@LoginActivity, deviceId)
                        loginResponse.driver_id?.let {
                            SessionManager.saveDriverId(this@LoginActivity, it)
                        }
                        loginResponse.rola?.let {
                            SessionManager.saveRole(this@LoginActivity, it)
                        }

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val fcmToken = task.result
                                api.updateFcmToken(fcmToken).enqueue(object : Callback<GenericResponse> {
                                    override fun onResponse(
                                        call: Call<GenericResponse>,
                                        response: Response<GenericResponse>
                                    ) {
                                        Log.d("LOGIN", "FCM token updated: ${response.body()?.message}")
                                    }

                                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                                        Log.e("LOGIN", "Failed to update FCM token: ${t.message}")
                                    }
                                })
                            } else {
                                Log.w("LOGIN", "Fetching FCM token failed", task.exception)
                            }

                            Toast.makeText(this@LoginActivity, "Zalogowano", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, ChooseVehicleActivity::class.java))
                            finish()
                        }
                    } else {
                        // prosty komunikat o błędzie logowania
                        Toast.makeText(this@LoginActivity, "Błąd logowania", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LOGIN", "Błąd sieci: ${t.message}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Błąd sieci: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun getRequiredPermissions(): Array<String> {
        val perms = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return perms.toTypedArray()
    }
    

    private fun isTokenValid(token: String, deviceId: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return false
            val payloadJson = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP))
            val obj = org.json.JSONObject(payloadJson)
            val exp = obj.optLong("exp", 0)
            val tokenDevice = obj.optString("device_id", "")
            val now = System.currentTimeMillis() / 1000
            exp > now && tokenDevice == deviceId
        } catch (e: Exception) {
            false
        }
    }

}
