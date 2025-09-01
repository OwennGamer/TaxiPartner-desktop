package com.partner.taxi

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

class DamageEditActivity : AppCompatActivity() {
    private lateinit var editNumber: EditText
    private lateinit var editDescription: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnSave: Button
    private lateinit var rvPhotos: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private val photoUris = mutableListOf<Uri>()
    private var currentPhotoUri: Uri? = null
    private lateinit var damage: DamageItem

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let {
                photoUris.add(it)
                photoAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_damage_edit)

        editNumber = findViewById(R.id.editDamageNumber)
        editDescription = findViewById(R.id.editDamageDescription)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSave = findViewById(R.id.btnUpdateDamage)
        rvPhotos = findViewById(R.id.rvPhotos)

        photoAdapter = PhotoAdapter(photoUris, ::launchCamera, ::previewPhoto) { index ->
            photoUris.removeAt(index)
            photoAdapter.notifyDataSetChanged()
        }
        rvPhotos.adapter = photoAdapter
        rvPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

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
        photoUris.clear()
        damage.zdjecia.forEach { photoUris.add(Uri.parse(it)) }
        photoAdapter.notifyDataSetChanged()

        btnSave.setOnClickListener {
            val nr = editNumber.text.toString()
            val opis = editDescription.text.toString()

            val status = spinnerStatus.selectedItem as String
            val idBody = damage.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val rejestracjaBody = damage.rejestracja.toRequestBody("text/plain".toMediaTypeOrNull())
            val nrBody = nr.toRequestBody("text/plain".toMediaTypeOrNull())
            val opisBody = opis.toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
            val parts = prepareParts()
            ApiClient.apiService.updateDamage(idBody, rejestracjaBody, nrBody, opisBody, statusBody, parts)
                .enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(this@DamageEditActivity, "Zaktualizowano", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
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

    private fun launchCamera() {
        val file = File.createTempFile("damage_", ".jpg", getExternalFilesDir(null))
        currentPhotoUri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        takePicture.launch(currentPhotoUri)
    }

    private fun previewPhoto(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun prepareParts(): List<MultipartBody.Part> {
        return photoUris.filter { it.scheme != "http" && it.scheme != "https" }
            .mapIndexedNotNull { index, uri ->
                try {
                    val input = contentResolver.openInputStream(uri) ?: return@mapIndexedNotNull null
                    var bitmap = BitmapFactory.decodeStream(input)
                    input.close()
                    val maxDim = 1280
                    val ratio = min(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
                    if (ratio < 1f) {
                        val newW = (bitmap.width * ratio).toInt()
                        val newH = (bitmap.height * ratio).toInt()
                        bitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
                    }
                    val file = File(cacheDir, "damage_up_$index.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                    }
                    val req = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photos[]", file.name, req)
                } catch (e: Exception) {
                    null
                }
            }
    }
}