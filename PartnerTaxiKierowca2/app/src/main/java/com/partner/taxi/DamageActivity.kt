package com.partner.taxi

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class DamageActivity : AppCompatActivity() {

    private lateinit var editDamageNumber: EditText
    private lateinit var editDamageDescription: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnSelectPhotos: Button
    private lateinit var tvPhotosCount: TextView
    private lateinit var btnSave: Button

    private val photoUris = mutableListOf<Uri>()
    private var currentPhotoUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let {
                photoUris.add(it)
                tvPhotosCount.text = "Wybrano ${photoUris.size} zdjęć"
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_damage)

        editDamageNumber = findViewById(R.id.editDamageNumber)
        editDamageDescription = findViewById(R.id.editDamageDescription)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSelectPhotos = findViewById(R.id.btnSelectPhotos)
        tvPhotosCount = findViewById(R.id.tvPhotosCount)
        btnSave = findViewById(R.id.btnSaveDamage)

        val statuses = arrayOf(
            "niezgłoszona",
            "zgłoszona",
            "czeka na wycenę",
            "czeka na naprawę",
            "czeka na rozliczenie",
            "zamknięta"
        )
        spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        btnSelectPhotos.setOnClickListener {
            val file = File.createTempFile("damage_", ".jpg", getExternalFilesDir(null))
            currentPhotoUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            takePicture.launch(currentPhotoUri)
        }

        btnSave.setOnClickListener {
            val nrSzkody = editDamageNumber.text.toString().trim()
            val opis = editDamageDescription.text.toString().trim()
            val status = spinnerStatus.selectedItem as String
            val rejestracja = intent.getStringExtra("rejestracja") ?: ""

            if (nrSzkody.isEmpty()) {
                Toast.makeText(this, "Podaj nr szkody", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (opis.isEmpty()) {
                Toast.makeText(this, "Podaj opis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (rejestracja.isEmpty()) {
                Toast.makeText(this, "Brak rejestracji", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadDamage(nrSzkody, opis, status, rejestracja)
        }
    }

    private fun uploadDamage(nrSzkody: String, opis: String, status: String, rejestracja: String) {
        val nrBody = nrSzkody.toRequestBody("text/plain".toMediaTypeOrNull())
        val opisBody = opis.toRequestBody("text/plain".toMediaTypeOrNull())
        val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
        val rejestracjaBody = rejestracja.toRequestBody("text/plain".toMediaTypeOrNull())

        val parts = photoUris.mapIndexedNotNull { index, uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File(cacheDir, "damage_$index.jpg")
                val outputStream = FileOutputStream(tempFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photos[]", tempFile.name, requestFile)
            } catch (e: Exception) {
                null
            }
        }

        ApiClient.apiService.addDamage(rejestracjaBody, nrBody, opisBody, statusBody, parts)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@DamageActivity, "Szkoda zapisana", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Błąd zapisu"
                        Toast.makeText(this@DamageActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@DamageActivity, "Błąd sieci: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }
}