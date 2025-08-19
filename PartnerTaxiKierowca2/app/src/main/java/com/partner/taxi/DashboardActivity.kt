package com.partner.taxi

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.partner.taxi.VehicleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tvLabelLicznik: TextView
    private lateinit var tvLicznik: TextView
    private lateinit var btnDodajKurs: MaterialButton
    private lateinit var btnHistoria: MaterialButton
    private lateinit var btnTankowanie: MaterialButton
    private lateinit var btnGrafik: MaterialButton
    private lateinit var btnFlota: MaterialButton
    private lateinit var btnPusty1: MaterialButton
    private lateinit var btnPusty2: MaterialButton
    private lateinit var btnPusty3: MaterialButton
    private lateinit var btnZakonczPrace: MaterialButton
    private var vehiclePlate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        vehiclePlate = intent.getStringExtra("rejestracja")
            ?: SessionManager.getVehiclePlate(this)
        if (intent.hasExtra("rejestracja")) {
            vehiclePlate?.let { SessionManager.saveVehiclePlate(this, it) }
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        tvLabelLicznik     = findViewById(R.id.tvLabelLicznik)
        tvLicznik          = findViewById(R.id.tvLicznik)
        btnDodajKurs       = findViewById(R.id.btnDodajKurs)
        btnHistoria        = findViewById(R.id.btnHistoria)
        btnTankowanie      = findViewById(R.id.btnTankowanie)
        btnGrafik          = findViewById(R.id.btnGrafik)
        btnFlota           = findViewById(R.id.btnFlota)
        btnPusty1          = findViewById(R.id.btnPusty1)
        btnPusty2          = findViewById(R.id.btnPusty2)
        btnPusty3          = findViewById(R.id.btnPusty3)
        btnZakonczPrace    = findViewById(R.id.btnZakonczPrace)

        val role = SessionManager.getRole(this).lowercase()

        btnFlota.setOnClickListener {
            if (role != "flotowiec") {
                Toast.makeText(this, "BRAK UPRAWNIEŃ", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, FleetActivity::class.java))
            }
        }

        if (role != "flotowiec") {
            btnFlota.alpha = 0.5f
        }



        // <-- PRZYWRACAMY LOSOWE KOLORY DLA KAFELKÓW -->
        val buttons = listOf(
            btnDodajKurs, btnHistoria, btnTankowanie, btnGrafik,
            btnFlota, btnPusty1, btnPusty2, btnPusty3
        )
        val colors = listOf(
            "#F44336", "#E91E63", "#9C27B0", "#673AB7",
            "#3F51B5", "#03A9F4", "#009688", "#8BC34A"
        ).shuffled()
        buttons.forEachIndexed { i, btn ->
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colors[i]))
        }
        // <-- KONIEC FRAGMENTU Z KOLORAMI -->

        // Listener do dodawania kursu
        btnDodajKurs.setOnClickListener {
            startActivity(Intent(this, AddRideActivity::class.java))
        }

        // Listener do historii
        btnHistoria.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Listener do tankowania
        btnTankowanie.setOnClickListener {
            startActivity(Intent(this, FuelActivity::class.java))
        }

        // Listener do zakończenia pracy
        btnZakonczPrace.setOnClickListener {
            val sessionId = SessionManager.getSessionId(this)
            if (sessionId.isNullOrEmpty()) {
                Toast.makeText(this, "Brak aktywnej sesji", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val input = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
            }

            AlertDialog.Builder(this)
                .setTitle("Podaj przebieg")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val odo = input.text.toString().toIntOrNull()
                    if (odo == null) {
                        Toast.makeText(this, "Nieprawidłowa wartość", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    validateAndEndShift(odo)
                }
                .setNegativeButton("Anuluj", null)
                .show()
        }

        swipeRefreshLayout.setOnRefreshListener {
            loadDriverSaldo { swipeRefreshLayout.isRefreshing = false }
        }

        // Wczytaj saldo od razu
        loadDriverSaldo()
    }

    override fun onResume() {
        super.onResume()
        loadDriverSaldo()
    }

    private fun loadDriverSaldo(onComplete: (() -> Unit)? = null) {
        val driverId = SessionManager.getDriverId(this)
        if (driverId.isNullOrEmpty()) {
            tvLicznik.text = "0.00 zł"
            onComplete?.invoke()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getDriverData(driverId)
                if (response.isSuccessful && response.body()?.status == "success") {
                    val saldo = response.body()?.data?.saldo ?: 0f
                    tvLicznik.text = String.format("%.2f zł", saldo)
                } else {
                    tvLicznik.text = "0.00 zł"
                    Toast.makeText(this@DashboardActivity, "Błąd pobierania salda", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                tvLicznik.text = "0.00 zł"
                Toast.makeText(this@DashboardActivity, "Brak połączenia", Toast.LENGTH_SHORT).show()
            } finally {
                onComplete?.invoke()
            }
        }
    }

    private fun endShiftAndUpdate(odometer: Int, plate: String) {
        ApiClient.apiService.endShift(odometer)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        SessionManager.clearSessionId(this@DashboardActivity)
                        SessionManager.clearVehiclePlate(this@DashboardActivity)
                        Toast.makeText(
                            this@DashboardActivity,
                            response.body()?.message ?: "Zakończono pracę",
                            Toast.LENGTH_SHORT
                        ).show()
                        ApiClient.apiService.updateVehicleMileage(plate, odometer)
                            .enqueue(object : Callback<GenericResponse> {
                                override fun onResponse(
                                    call: Call<GenericResponse>,
                                    resp: Response<GenericResponse>
                                ) { /* brak akcji */ }

                                override fun onFailure(call: Call<GenericResponse>, t: Throwable) { }
                            })
                        startActivity(Intent(this@DashboardActivity, ChooseVehicleActivity::class.java))
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Błąd zakończenia"
                        Toast.makeText(this@DashboardActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Błąd sieci: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    private fun validateAndEndShift(odometer: Int) {
        val plate = vehiclePlate
        if (plate.isNullOrEmpty()) {
            Toast.makeText(this, "Brak informacji o pojeździe", Toast.LENGTH_SHORT).show()
            return
        }
        ApiClient.apiService.getVehicleInfo(plate)
            .enqueue(object : Callback<VehicleResponse> {
                override fun onResponse(
                    call: Call<VehicleResponse>,
                    response: Response<VehicleResponse>
                ) {
                    val current = response.body()?.data?.przebieg
                    if (!response.isSuccessful || current == null) {
                        Toast.makeText(this@DashboardActivity, "Błąd danych pojazdu", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (odometer < current) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Przebieg nie może być mniejszy niż $current",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    endShiftAndUpdate(odometer, plate)
                }

                override fun onFailure(call: Call<VehicleResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Błąd sieci: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}
