package com.partner.taxi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    private val photoFiles = mutableListOf<File>()
    private var currentPhotoFile: File? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoFile?.let {
                photoFiles.add(it)
                tvPhotosCount.text = "Wybrano ${photoFiles.size} zdjęć"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
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
            currentPhotoFile = file
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
            takePicture.launch(uri)
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

        val compressed = mutableListOf<File>()
        val parts = photoFiles.mapNotNull { file ->
            try {
                val cmp = compressFile(file)
                file.delete()
                cmp?.let {
                    compressed.add(it)
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photos[]", it.name, requestFile)
                }
            } catch (e: Exception) {
                null
            }
        }

        ApiClient.apiService.addDamage(rejestracjaBody, nrBody, opisBody, statusBody, parts)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>,
                ) {
                    compressed.forEach { it.delete() }
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@DamageActivity, "Szkoda zapisana", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val msg = response.body()?.message ?: "Błąd zapisu"
                        Toast.makeText(this@DamageActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    compressed.forEach { it.delete() }
                    Toast.makeText(this@DamageActivity, "Błąd sieci: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
    }
    private fun scaleBitmap(src: Bitmap, maxSize: Int): Bitmap {
        val (w, h) = src.width to src.height
        val ratio = w.toFloat() / h
        val (nw, nh) = if (ratio > 1) {
            maxSize to (maxSize / ratio).toInt()
        } else {
            (maxSize * ratio).toInt() to maxSize
        }
        return Bitmap.createScaledBitmap(src, nw, nh, true)
    }

    private fun compressFile(orig: File?): File? = orig?.let {
        val bmp = BitmapFactory.decodeFile(it.absolutePath)
        val scaled = scaleBitmap(bmp, 1024)
        val out = File(cacheDir, "comp_${it.name}")
        FileOutputStream(out).use { fos ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }
        out
    }
}