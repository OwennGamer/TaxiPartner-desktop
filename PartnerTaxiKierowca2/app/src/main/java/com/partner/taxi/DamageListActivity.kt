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

class DamageListActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private var damages: List<DamageItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_damage_list)

        listView = findViewById(R.id.listDamages)

        val rejestracja = intent.getStringExtra("rejestracja") ?: ""
        if (rejestracja.isNotEmpty()) {
            ApiClient.apiService.getDamages(rejestracja)
                .enqueue(object : Callback<DamagesResponse> {
                    override fun onResponse(
                        call: Call<DamagesResponse>,
                        response: Response<DamagesResponse>
                    ) {
                        if (response.isSuccessful) {
                            damages = response.body()?.damages?.map { item ->
                                val photos = item.zdjecia.map { photo ->
                                    if (photo.startsWith("http")) photo else ApiClient.BASE_URL + photo
                                }
                                item.copy(zdjecia = photos)
                            } ?: emptyList()
                            val items = damages.map {
                                "${it.nr_szkody} - ${it.status}"
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
                        Toast.makeText(
                            this@DamageListActivity,
                            t.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val item = damages[position]
            val intent = Intent(this, DamageEditActivity::class.java)
            intent.putExtra("damage", item)
            startActivity(intent)
        }
    }
}