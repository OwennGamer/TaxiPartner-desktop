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
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_service_list)

        listView = findViewById(R.id.listServices)

        val rejestracja = intent.getStringExtra("rejestracja") ?: ""
        if (rejestracja.isNotEmpty()) {
            loadServices(rejestracja)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = services[position]
            val intent = Intent(this, ServiceEditActivity::class.java)
            intent.putExtra("service", item)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val rejestracja = intent.getStringExtra("rejestracja") ?: return
        loadServices(rejestracja)
    }

    private fun loadServices(rejestracja: String) {
        ApiClient.apiService.getServices(rejestracja)
            .enqueue(object : Callback<ServicesResponse> {
                override fun onResponse(
                    call: Call<ServicesResponse>,
                    response: Response<ServicesResponse>,
                ) {
                    if (response.isSuccessful) {
                        services = response.body()?.services?.map { item ->
                            item.copy(zdjecia = prefixPhotoPaths(item.zdjecia))
                        } ?: emptyList()
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
                    this@ServiceListActivity.showConnectionIssueToast(t)
                }
            })
    }

    private fun prefixPhotoPaths(paths: List<String>): List<String> {
        val base = ApiClient.BASE_URL.replace("api/", "")
        return paths.map { photo ->
            if (photo.startsWith("http")) photo else base + photo.trimStart('/')
        }
    }
}