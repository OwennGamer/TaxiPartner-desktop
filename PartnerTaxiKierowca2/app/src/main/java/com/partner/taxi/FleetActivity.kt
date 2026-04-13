package com.partner.taxi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class FleetActivity : AppCompatActivity() {
    private lateinit var rvFleet: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_fleet)

        supportActionBar?.apply {
            title = "Flota"
            setDisplayHomeAsUpEnabled(true)
        }

        rvFleet = findViewById(R.id.rvFleet)
        rvFleet.layoutManager = LinearLayoutManager(this)

        loadVehicles()
    }

    private fun loadVehicles() {
        lifecycleScope.launch {
            try {
                Log.d("FleetActivity", "Fetching vehicles")
                val response = ApiClient.apiService.getVehicles()
                Log.d("FleetActivity", "Response status: ${response.status}")
                if (response.status == "success") {
                    rvFleet.adapter = FleetAdapter(response.data)
                } else {
                    Toast.makeText(this@FleetActivity, "Błąd: ${response.status}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("FleetActivity", "Network error while loading vehicles", e)
                this@FleetActivity.showConnectionIssueToast(e)
            } catch (e: JsonSyntaxException) {
                Log.e("FleetActivity", "Data parsing error while loading vehicles", e)
                Toast.makeText(
                    this@FleetActivity,
                    "Błąd danych serwera",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("FleetActivity", "Unexpected error while loading vehicles", e)
                Toast.makeText(
                    this@FleetActivity,
                    "Nieoczekiwany błąd",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private class FleetAdapter(private val items: List<VehicleData>) :
        RecyclerView.Adapter<FleetAdapter.ViewHolder>() {
        class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
            val tvPlate: android.widget.TextView = itemView.findViewById(R.id.tvPlate)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                inflate(R.layout.item_vehicle_fleet, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val vehicle = items[position]
            val oilDue = isOilChangeDue(vehicle)
            holder.tvPlate.text = if (oilDue) {
                "${vehicle.rejestracja} - wymiana oleju"
            } else {
                vehicle.rejestracja
            }
            holder.tvPlate.setTextColor(
                if (oilDue) android.graphics.Color.RED else android.graphics.Color.BLACK
            )
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, VehicleDetailActivity::class.java)
                intent.putExtra("vehicle", vehicle)
                context.startActivity(intent)
            }
        }

        private fun isOilChangeDue(vehicle: VehicleData): Boolean {
            val daysLeft = vehicle.wymiana_oleju_data?.let {
                runCatching {
                    ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(it)).toInt()
                }.getOrNull()
            }
            val kmLeft = vehicle.wymiana_oleju_przebieg?.minus(vehicle.przebieg)
            val byDate = daysLeft != null && daysLeft <= 7
            val byKm = kmLeft != null && kmLeft <= 500
            return byDate || byKm
        }

        override fun getItemCount(): Int = items.size
    }
}
