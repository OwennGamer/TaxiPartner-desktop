package com.partner.taxi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DamageEditActivity : AppCompatActivity() {
    private lateinit var editNumber: EditText
    private lateinit var editDescription: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnPhotos: Button
    private lateinit var btnSave: Button
    private lateinit var damage: DamageItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_damage_edit)

        editNumber = findViewById(R.id.editDamageNumber)
        editDescription = findViewById(R.id.editDamageDescription)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnPhotos = findViewById(R.id.btnShowPhotos)
        btnSave = findViewById(R.id.btnUpdateDamage)

        val statuses = arrayOf(
            "niezgłoszona",
            "zgłoszona",
            "czeka na wycenę",
            "czeka na naprawę",
            "czeka na rozliczenie",
            "zamknięta"
        )
        spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        damage = intent.getSerializableExtra("damage") as DamageItem
        editNumber.setText(damage.nr_szkody)
        editDescription.setText(damage.opis)
        val index = statuses.indexOf(damage.status)
        if (index >= 0) spinnerStatus.setSelection(index)

        btnPhotos.setOnClickListener {
            if (damage.zdjecia.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(damage.zdjecia[0]))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Brak zdjęć", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val nr = editNumber.text.toString()
            val opis = editDescription.text.toString()

            val status = spinnerStatus.selectedItem as String
            ApiClient.apiService.updateDamage(damage.id, damage.rejestracja, nr, opis, status)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@DamageEditActivity, "Zaktualizowano", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@DamageEditActivity,
                                response.body()?.message ?: "Błąd",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(
                            this@DamageEditActivity,
                            t.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}