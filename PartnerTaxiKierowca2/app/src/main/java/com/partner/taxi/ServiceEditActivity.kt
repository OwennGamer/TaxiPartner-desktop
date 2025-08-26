package com.partner.taxi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceEditActivity : AppCompatActivity() {
    private lateinit var editDescription: EditText
    private lateinit var editCost: EditText
    private lateinit var btnPhotos: Button
    private lateinit var btnSave: Button
    private lateinit var service: ServiceItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_service_edit)

        editDescription = findViewById(R.id.editDescription)
        editCost = findViewById(R.id.editCost)
        btnPhotos = findViewById(R.id.btnShowPhotos)
        btnSave = findViewById(R.id.btnUpdateService)

        service = intent.getSerializableExtra("service") as ServiceItem
        editDescription.setText(service.opis)
        editCost.setText(service.koszt.toString())

        btnPhotos.setOnClickListener {
            if (service.zdjecia.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(service.zdjecia[0]))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Brak zdjęć", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val opis = editDescription.text.toString()
            val koszt = editCost.text.toString().toFloatOrNull()
            if (koszt == null) {
                Toast.makeText(this, "Podaj koszt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ApiClient.apiService.updateService(service.id, opis, koszt)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@ServiceEditActivity, "Zaktualizowano", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@ServiceEditActivity,
                                response.body()?.message ?: "Błąd",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                        Toast.makeText(
                            this@ServiceEditActivity,
                            t.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        }
    }
}