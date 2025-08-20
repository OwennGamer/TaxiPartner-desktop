package com.partner.taxi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FleetActivity : AppCompatActivity() {
    private lateinit var rvFleet: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            } catch (e: Exception) {
                Log.e("FleetActivity", "Error loading vehicles", e)
                Toast.makeText(this@FleetActivity, "Brak połączenia", Toast.LENGTH_SHORT).show()
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
            val tvPlate: android.widget.TextView = itemView.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
            val view = android.view.LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val vehicle = items[position]
            holder.tvPlate.text = vehicle.rejestracja
            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, VehicleDetailActivity::class.java)
                intent.putExtra("vehicle", vehicle)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
