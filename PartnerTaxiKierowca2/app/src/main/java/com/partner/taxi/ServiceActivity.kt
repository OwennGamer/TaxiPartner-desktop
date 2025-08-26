package com.partner.taxi

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

class ServiceActivity : AppCompatActivity() {

    private lateinit var editDescription: EditText
    private lateinit var editCost: EditText
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
        setContentView(R.layout.activity_service)

        editDescription = findViewById(R.id.editDescription)
        editCost = findViewById(R.id.editCost)
        btnSelectPhotos = findViewById(R.id.btnSelectPhotos)
        tvPhotosCount = findViewById(R.id.tvPhotosCount)
        btnSave = findViewById(R.id.btnSaveService)

        btnSelectPhotos.setOnClickListener {
            val file = File.createTempFile("service_", ".jpg", getExternalFilesDir(null))
            currentPhotoUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            takePicture.launch(currentPhotoUri)
        }

        btnSave.setOnClickListener {
            val opis = editDescription.text.toString().trim()
            val koszt = editCost.text.toString().trim().toFloatOrNull()
            val rejestracja = intent.getStringExtra("rejestracja") ?: ""

            if (opis.isEmpty()) {
                Toast.makeText(this, "Podaj opis", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (koszt == null) {
                Toast.makeText(this, "Podaj poprawny koszt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (rejestracja.isEmpty()) {
                Toast.makeText(this, "Brak rejestracji", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadService(opis, koszt, rejestracja)
        }
    }

    private fun uploadService(opis: String, koszt: Float, rejestracja: String) {
        val opisBody = opis.toRequestBody("text/plain".toMediaTypeOrNull())
        val kosztBody = koszt.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val rejestracjaBody = rejestracja.toRequestBody("text/plain".toMediaTypeOrNull())

        val parts = photoUris.mapIndexedNotNull { index, uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File(cacheDir, "photo_$index.jpg")
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

        ApiClient.apiService.addService(opisBody, kosztBody, rejestracjaBody, parts)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@ServiceActivity, "Serwis zapisany", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Błąd zapisu"
                        Toast.makeText(this@ServiceActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    Toast.makeText(this@ServiceActivity, "Błąd sieci: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }
}