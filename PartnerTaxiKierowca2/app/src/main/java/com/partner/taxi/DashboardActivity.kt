package com.partner.taxi

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

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

        // Listener do zakończenia pracy (jeśli potrzeba)
        btnZakonczPrace.setOnClickListener {
            // tu możesz dodać logikę wylogowania
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
}
