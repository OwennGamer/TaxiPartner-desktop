package com.partner.taxi

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

class ServiceEditActivity : AppCompatActivity() {
    private lateinit var editDescription: EditText
    private lateinit var editCost: EditText
    private lateinit var btnSave: Button
    private lateinit var rvPhotos: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private val photoUris = mutableListOf<Uri>()
    private var currentPhotoUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let {
                photoUris.add(it)
                photoAdapter.notifyDataSetChanged()
            }
        }
    }
    private lateinit var service: ServiceItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_service_edit)

        editDescription = findViewById(R.id.editDescription)
        editCost = findViewById(R.id.editCost)
        btnSave = findViewById(R.id.btnUpdateService)
        rvPhotos = findViewById(R.id.rvPhotos)

        photoAdapter = PhotoAdapter(photoUris, ::launchCamera, ::previewPhoto) { index ->
            photoUris.removeAt(index)
            photoAdapter.notifyDataSetChanged()
        }
        rvPhotos.adapter = photoAdapter
        rvPhotos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val passed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("service", ServiceItem::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra<ServiceItem>("service")
        }
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
                    this@ServiceEditActivity.showConnectionIssueToast(t)
                    finish()
                }
            })
    }

    private fun setupService(item: ServiceItem) {
        service = item
        editDescription.setText(service.opis)
        editCost.setText(service.koszt.toString())

        photoUris.clear()
        service.zdjecia.forEach { photoUris.add(Uri.parse(it)) }
        photoAdapter.notifyDataSetChanged()

        btnSave.setOnClickListener {
            val opis = editDescription.text.toString()
            val koszt = editCost.text.toString().toFloatOrNull()
            if (koszt == null) {
                Toast.makeText(this@ServiceEditActivity, "Podaj koszt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val idBody = service.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val opisBody = opis.toRequestBody("text/plain".toMediaTypeOrNull())
            val kosztBody = koszt.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val parts = prepareParts()
            ApiClient.apiService.updateService(idBody, opisBody, kosztBody, parts)
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
                        this@ServiceEditActivity.showConnectionIssueToast(t)
                    }
                })
        }
    }

    private fun launchCamera() {
        val file = File.createTempFile("service_", ".jpg", getExternalFilesDir(null))
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
                    val file = File(cacheDir, "service_up_$index.jpg")
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