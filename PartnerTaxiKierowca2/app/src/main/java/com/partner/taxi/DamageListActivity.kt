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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DamageListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private var damages: List<DamageItem> = emptyList()
    private lateinit var rejestracja: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_damage_list)

        listView = findViewById(R.id.listDamages)

        rejestracja = intent.getStringExtra("rejestracja") ?: ""
        if (rejestracja.isNotEmpty()) {
            loadDamages()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = damages[position]
            val intent = Intent(this, DamageEditActivity::class.java)
            intent.putExtra("damage", item)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::rejestracja.isInitialized && rejestracja.isNotEmpty()) {
            loadDamages()
        }
    }

    private fun loadDamages() {
        ApiClient.apiService.getDamages(rejestracja)
            .enqueue(object : Callback<DamagesResponse> {
                override fun onResponse(
                    call: Call<DamagesResponse>,
                    response: Response<DamagesResponse>,
                ) {
                    if (response.isSuccessful) {
                        damages = response.body()?.damages?.map { item ->
                            item.copy(zdjecia = prefixPhotoPaths(item.zdjecia))
                        } ?: emptyList()
                        val items = damages.map {
                            "${it.nr_szkody} - ${it.status} - ${formatDate(it.data)}"
                        }
                        listView.adapter = ArrayAdapter(
                            this@DamageListActivity,
                            android.R.layout.simple_list_item_1,
                            items
                        )
                    } else {
                        Toast.makeText(
                            this@DamageListActivity,
                            "Błąd pobierania",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DamagesResponse>, t: Throwable) {
                    this@DamageListActivity.showConnectionIssueToast(t)
                }
            })
    }
    private fun prefixPhotoPaths(paths: List<String>): List<String> {
        val base = ApiClient.BASE_URL.replace("api/", "")
        return paths.map { photo ->
            if (photo.startsWith("http")) photo else base + photo.trimStart('/')
        }
    }
    private fun formatDate(dateString: String): String {
        return try {
            LocalDate.parse(dateString).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (e: Exception) {
            dateString
        }
    }
}
