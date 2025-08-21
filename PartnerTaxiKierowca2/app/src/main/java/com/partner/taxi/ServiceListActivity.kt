package com.partner.taxi

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private var services: List<ServiceItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_list)

        listView = findViewById(R.id.listServices)

        val rejestracja = intent.getStringExtra("rejestracja") ?: ""
        if (rejestracja.isNotEmpty()) {
            ApiClient.apiService.getServices(rejestracja)
                .enqueue(object : Callback<ServicesResponse> {
                    override fun onResponse(
                        call: Call<ServicesResponse>,
                        response: Response<ServicesResponse>
                    ) {
                        if (response.isSuccessful) {
                            services = response.body()?.services ?: emptyList()
                            val items = services.map {
                                "${it.data}: ${it.opis} (${it.koszt})"
                            }
                            listView.adapter = ArrayAdapter(
                                this@ServiceListActivity,
                                android.R.layout.simple_list_item_1,
                                items
                            )
                        } else {
                            Toast.makeText(
                                this@ServiceListActivity,
                                "Błąd pobierania",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ServicesResponse>, t: Throwable) {
                        Toast.makeText(
                            this@ServiceListActivity,
                            t.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = services[position]
            val intent = Intent(this, ServiceEditActivity::class.java)
            intent.putExtra("service", item)
            startActivity(intent)
        }
    }
}