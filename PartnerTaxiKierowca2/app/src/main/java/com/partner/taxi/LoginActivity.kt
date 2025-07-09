package com.partner.taxi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val api = ApiClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            api.login(user, pass).enqueue(object : Callback<LoginResponse> {
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
                        // zapisujemy token dla interceptor’a
                        ApiClient.jwtToken = loginResponse.token

                        // opcjonalnie trzymamy w SharedPreferences
                        SessionManager.saveToken(this@LoginActivity, loginResponse.token)
                        loginResponse.driver_id?.let {
                            SessionManager.saveDriverId(this@LoginActivity, it)
                        }

                        Toast.makeText(this@LoginActivity, "Zalogowano", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, ChooseVehicleActivity::class.java))
                        finish()
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
}
