package com.partner.taxi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FuelActivity : AppCompatActivity() {

    private lateinit var editLiters: EditText
    private lateinit var editCost: EditText
    private lateinit var editOdo: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fuel)

        // znajdź widoki
        editLiters = findViewById(R.id.editLiters)
        editCost   = findViewById(R.id.editCost)
        editOdo    = findViewById(R.id.editOdo)
        btnSave    = findViewById(R.id.btnSaveFuel)

        btnSave.setOnClickListener {
            val liters = editLiters.text.toString().toFloatOrNull()
            val cost   = editCost.text.toString().toFloatOrNull()
            val odo    = editOdo.text.toString().toIntOrNull()

            if (liters == null || liters <= 0f) {
                Toast.makeText(this, "Podaj poprawną ilość litrów", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (cost == null || cost <= 0f) {
                Toast.makeText(this, "Podaj poprawny koszt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (odo == null || odo < 0) {
                Toast.makeText(this, "Podaj poprawny przebieg", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // wywołanie API
            ApiClient.apiService.addRefuel(liters, cost, odo)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@FuelActivity,
                                "Tankowanie zapisane", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val msg = response.body()?.message
                                ?: "Nieoczekiwana odpowiedź serwera"
                            Toast.makeText(this@FuelActivity,
                                "Błąd: $msg", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(this@FuelActivity,
                            "Błąd sieci: ${t.localizedMessage}",
                            Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
