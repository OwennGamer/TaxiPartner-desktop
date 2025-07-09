package com.partner.taxi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        supportActionBar?.apply {
            title = "Historia kursów"
            setDisplayHomeAsUpEnabled(true)
        }

        rvHistory = findViewById(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)

        loadHistory()
    }

    private fun loadHistory() {
        val driverId = SessionManager.getDriverId(this)
        if (driverId.isNullOrEmpty()) {
            Toast.makeText(this, "Brak ID kierowcy", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getRideHistory(driverId)

                if (response.status == "success") {
                    // Ustawiamy adapter na wszystkie zwrócone kursy
                    Log.d("HistoryActivity", "Mamy ${response.data.size} przejazdów")
                    rvHistory.adapter = HistoryAdapter(response.data)
                } else {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Błąd: ${response.status}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@HistoryActivity,
                    "Brak połączenia",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: HttpException) {
                Toast.makeText(
                    this@HistoryActivity,
                    "Błąd serwera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
