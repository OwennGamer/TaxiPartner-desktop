package com.partner.taxi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class ReportActivity : AppCompatActivity() {

    private lateinit var serviceDescriptionLayout: TextInputLayout
    private lateinit var serviceDescriptionInput: TextInputEditText
    private lateinit var servicePhotoStatus: TextView
    private lateinit var serviceTakePhotoButton: MaterialButton
    private lateinit var serviceSendButton: MaterialButton

    private lateinit var otherDescriptionLayout: TextInputLayout
    private lateinit var otherDescriptionInput: TextInputEditText
    private lateinit var otherPhotoStatus: TextView
    private lateinit var otherTakePhotoButton: MaterialButton
    private lateinit var otherSendButton: MaterialButton

    private var vehiclePlate: String? = null

    private var servicePhotoFile: File? = null
    private var pendingServicePhoto: File? = null
    private var otherPhotoFile: File? = null
    private var pendingOtherPhoto: File? = null

    private val serviceTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        handlePhotoResult(success, ReportType.SERVICE)
    }

    private val otherTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        handlePhotoResult(success, ReportType.OTHER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_report)

        title = getString(R.string.report_title)

        vehiclePlate = intent.getStringExtra("rejestracja")
            ?: SessionManager.getVehiclePlate(this)

        bindViews()
        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        listOf(servicePhotoFile, pendingServicePhoto, otherPhotoFile, pendingOtherPhoto).forEach { file ->
            file?.delete()
        }
    }

    private fun bindViews() {
        serviceDescriptionLayout = findViewById(R.id.layoutServiceDescription)
        serviceDescriptionInput = findViewById(R.id.inputServiceDescription)
        servicePhotoStatus = findViewById(R.id.tvServicePhotoStatus)
        serviceTakePhotoButton = findViewById(R.id.btnServicePhoto)
        serviceSendButton = findViewById(R.id.btnServiceSend)

        otherDescriptionLayout = findViewById(R.id.layoutOtherDescription)
        otherDescriptionInput = findViewById(R.id.inputOtherDescription)
        otherPhotoStatus = findViewById(R.id.tvOtherPhotoStatus)
        otherTakePhotoButton = findViewById(R.id.btnOtherPhoto)
        otherSendButton = findViewById(R.id.btnOtherSend)
    }

    private fun setupListeners() {
        serviceTakePhotoButton.setOnClickListener { launchCamera(ReportType.SERVICE) }
        otherTakePhotoButton.setOnClickListener { launchCamera(ReportType.OTHER) }

        serviceSendButton.setOnClickListener { submitReport(ReportType.SERVICE) }
        otherSendButton.setOnClickListener { submitReport(ReportType.OTHER) }
    }

    private fun launchCamera(type: ReportType) {
        val tempFile = File.createTempFile(
            "report_${type.apiValue.lowercase(Locale.ROOT)}_",
            ".jpg",
            getExternalFilesDir(null)
        )
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", tempFile)
        when (type) {
            ReportType.SERVICE -> {
                pendingServicePhoto?.delete()
                pendingServicePhoto = tempFile
                serviceTakePicture.launch(uri)
            }

            ReportType.OTHER -> {
                pendingOtherPhoto?.delete()
                pendingOtherPhoto = tempFile
                otherTakePicture.launch(uri)
            }
        }
    }

    private fun handlePhotoResult(success: Boolean, type: ReportType) {
        when (type) {
            ReportType.SERVICE -> {
                if (success) {
                    servicePhotoFile?.delete()
                    servicePhotoFile = pendingServicePhoto
                    servicePhotoStatus.text = getString(R.string.report_photo_status_added)
                } else {
                    pendingServicePhoto?.delete()
                }
                pendingServicePhoto = null
            }

            ReportType.OTHER -> {
                if (success) {
                    otherPhotoFile?.delete()
                    otherPhotoFile = pendingOtherPhoto
                    otherPhotoStatus.text = getString(R.string.report_photo_status_added)
                } else {
                    pendingOtherPhoto?.delete()
                }
                pendingOtherPhoto = null
            }
        }
    }

    private fun submitReport(type: ReportType) {
        val descriptionLayout = if (type == ReportType.SERVICE) {
            serviceDescriptionLayout
        } else {
            otherDescriptionLayout
        }
        val descriptionInput = if (type == ReportType.SERVICE) {
            serviceDescriptionInput
        } else {
            otherDescriptionInput
        }
        val photoFile = if (type == ReportType.SERVICE) {
            servicePhotoFile
        } else {
            otherPhotoFile
        }
        val sendButton = if (type == ReportType.SERVICE) {
            serviceSendButton
        } else {
            otherSendButton
        }

        val description = descriptionInput.text?.toString()?.trim().orEmpty()
        if (description.isEmpty()) {
            descriptionLayout.error = getString(R.string.report_error_description)
            return
        } else {
            descriptionLayout.error = null
        }

        val plate = vehiclePlate?.takeIf { it.isNotBlank() }
        if (plate == null) {
            Toast.makeText(this, R.string.report_error_vehicle_plate, Toast.LENGTH_LONG).show()
            return
        }

        val driverId = SessionManager.getDriverId(this)

        val textMediaType = "text/plain".toMediaTypeOrNull()
        val typeBody = type.apiValue.toRequestBody(textMediaType)
        val descriptionBody = description.toRequestBody(textMediaType)
        val driverBody = driverId.toRequestBody(textMediaType)
        val plateBody = plate.toRequestBody(textMediaType)

        val compressedFiles = mutableListOf<File>()
        val photoPart = photoFile?.let { original ->
            val compressed = compressFile(original)
            if (compressed != null) {
                compressedFiles.add(compressed)
                val requestBody = compressed.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", compressed.name, requestBody)
            } else {
                null
            }
        }

        sendButton.isEnabled = false

        ApiClient.apiService.sendReport(
            typeBody,
            descriptionBody,
            driverBody,
            plateBody,
            photoPart
        ).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                sendButton.isEnabled = true
                compressedFiles.forEach { it.delete() }

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@ReportActivity, R.string.report_send_success, Toast.LENGTH_SHORT).show()
                    descriptionInput.setText("")
                    if (type == ReportType.SERVICE) {
                        servicePhotoFile?.delete()
                        servicePhotoFile = null
                        servicePhotoStatus.text = getString(R.string.report_photo_status_none)
                    } else {
                        otherPhotoFile?.delete()
                        otherPhotoFile = null
                        otherPhotoStatus.text = getString(R.string.report_photo_status_none)
                    }
                } else {
                    val message = response.body()?.message ?: getString(R.string.report_send_error)
                    Toast.makeText(this@ReportActivity, message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                sendButton.isEnabled = true
                compressedFiles.forEach { it.delete() }
                this@ReportActivity.showConnectionIssueToast(t)
            }
        })
    }

    private fun compressFile(original: File?): File? {
        if (original == null || !original.exists()) return null
        val bitmap = BitmapFactory.decodeFile(original.absolutePath) ?: return null
        val scaled = scaleBitmap(bitmap, 1280)
        val compressed = File(cacheDir, "cmp_${original.name}")
        FileOutputStream(compressed).use { stream ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        }
        return compressed
    }

    private fun scaleBitmap(source: Bitmap, maxSize: Int): Bitmap {
        val width = source.width
        val height = source.height
        if (width <= maxSize && height <= maxSize) {
            return source
        }
        val ratio = width.toFloat() / height.toFloat()
        val (targetWidth, targetHeight) = if (ratio > 1) {
            maxSize to (maxSize / ratio).toInt()
        } else {
            (maxSize * ratio).toInt() to maxSize
        }
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }

    private enum class ReportType(val apiValue: String) {
        SERVICE("SERWIS"),
        OTHER("INNE")
    }
}