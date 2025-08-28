package com.partner.taxi

import android.app.Activity
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

        val passed = intent.getParcelableExtra<ServiceItem>("service")
        if (passed != null) {
            setupService(passed)
            return
        }

        val serviceId = intent.getIntExtra("service_id", -1)
        if (serviceId == -1) {
            Toast.makeText(this, "Brak ID serwisu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        ApiClient.apiService.getServiceDetail(serviceId)
            .enqueue(object : Callback<ServiceDetailResponse> {
                override fun onResponse(
                    call: Call<ServiceDetailResponse>,
                    response: Response<ServiceDetailResponse>,
                ) {
                    if (response.isSuccessful) {
                        val item = response.body()?.service
                        if (item != null) {
                            val base = ApiClient.BASE_URL.replace("api/", "")
                            val photos = item.zdjecia.map { photo ->
                                if (photo.startsWith("http")) photo else base + photo
                            }
                            setupService(item.copy(zdjecia = photos))
                        } else {
                            Toast.makeText(this@ServiceEditActivity, "Brak danych", Toast.LENGTH_LONG).show()
                            finish()
                        }
                    } else {
                        Toast.makeText(this@ServiceEditActivity, "Błąd pobierania", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<ServiceDetailResponse>, t: Throwable) {
                    Toast.makeText(this@ServiceEditActivity, t.localizedMessage, Toast.LENGTH_LONG).show()
                    finish()
                }
            })
    }

    private fun setupService(item: ServiceItem) {
        service = item
        editDescription.setText(service.opis)
        editCost.setText(service.koszt.toString())

        btnPhotos.setOnClickListener {
            if (service.zdjecia.isNotEmpty()) {
                val uris = ArrayList<Uri>().apply {
                    service.zdjecia.forEach { add(Uri.parse(it)) }
                }
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uris[0], "image/*")
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this@ServiceEditActivity, "Brak zdjęć", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val opis = editDescription.text.toString()
            val koszt = editCost.text.toString().toFloatOrNull()
            if (koszt == null) {
                Toast.makeText(this@ServiceEditActivity, "Podaj koszt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ApiClient.apiService.updateService(service.id, opis, koszt)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>,
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@ServiceEditActivity, "Zaktualizowano", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
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