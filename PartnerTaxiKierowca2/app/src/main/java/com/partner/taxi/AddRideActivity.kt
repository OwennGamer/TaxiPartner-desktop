package com.partner.taxi

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.Gravity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlin.math.min
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddRideActivity : AppCompatActivity() {

    private lateinit var spinnerSource: Spinner
    private lateinit var spinnerPaymentType: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var radioGroupAmountOrKm: RadioGroup
    private lateinit var radioAmount: RadioButton
    private lateinit var radioKm: RadioButton
    private lateinit var editTextKm: EditText
    private lateinit var receiptPreview: ImageView
    private lateinit var textReceiptPhotosCount: TextView
    private lateinit var buttonAddRide: Button
    private lateinit var buttonAddReceiptPhoto: Button
    private lateinit var buttonRetakePhoto: Button

    private val receiptPhotoPaths = mutableListOf<String>()
    private var pendingPhotoPath: String? = null
    private var pendingReceiptPromptRes: Int? = null
    private var isEditMode: Boolean = false

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val messageRes = pendingReceiptPromptRes
            pendingReceiptPromptRes = null
            if (granted) {
                messageRes?.let {
                    Toast.makeText(
                        this,
                        getString(it),
                        Toast.LENGTH_LONG
                    ).show()
                }
                launchCamera()
            } else {
                Toast.makeText(this, "Brak dostępu do kamery", Toast.LENGTH_SHORT).show()
                buttonAddReceiptPhoto.visibility = View.VISIBLE
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                pendingPhotoPath?.let { path ->
                    var bitmap = BitmapFactory.decodeFile(path)
                    val maxDim = 1280
                    val ratio = min(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
                    if (ratio < 1f) {
                        val newW = (bitmap.width * ratio).toInt()
                        val newH = (bitmap.height * ratio).toInt()
                        bitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
                        FileOutputStream(path).use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                        }
                    }
                    receiptPreview.setImageBitmap(bitmap)
                    receiptPreview.visibility = View.VISIBLE
                    receiptPhotoPaths.add(path)
                    pendingPhotoPath = null
                    updateReceiptPhotosUi()
                }
                showReceiptConfirmationDialog()
            } else {
                pendingPhotoPath?.let { File(it).delete() }
                pendingPhotoPath = null
                updateReceiptPhotosUi()
            }
        }



    companion object {
        const val EXTRA_EDIT_LAST_RIDE = "extra_edit_last_ride"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_add_ride)

        spinnerSource = findViewById(R.id.spinnerSource)
        spinnerPaymentType = findViewById(R.id.spinnerPaymentType)
        editTextAmount = findViewById(R.id.editTextAmount)
        radioGroupAmountOrKm = findViewById(R.id.radioGroupAmountOrKm)
        radioAmount = findViewById(R.id.radioAmount)
        radioKm = findViewById(R.id.radioKm)
        editTextKm = findViewById(R.id.editTextKm)
        receiptPreview = findViewById(R.id.receiptPreview)
        textReceiptPhotosCount = findViewById(R.id.textReceiptPhotosCount)
        buttonRetakePhoto = findViewById(R.id.buttonRetakePhoto)
        buttonAddRide = findViewById(R.id.buttonAddRide)
        buttonAddReceiptPhoto = findViewById(R.id.buttonAddReceiptPhoto)

        buttonAddReceiptPhoto.setOnClickListener {
            requestReceiptPhoto(isCardPayment())
        }

        // 🔵 Ładowanie danych do spinnerów
        val sourceAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.source_array,
            android.R.layout.simple_spinner_item
        )
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSource.adapter = sourceAdapter

        val paymentAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.payment_array,
            android.R.layout.simple_spinner_item
        )
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentType.adapter = paymentAdapter


        // 🔵 Kliknięcie przycisku "Dodaj kurs"
        buttonAddRide.setOnClickListener {
            val payment = spinnerPaymentType.selectedItem?.toString() ?: ""
            if (!isEditMode && payment == "Karta" && receiptPhotoPaths.isEmpty()) {
                requestReceiptPhoto(true)

            } else {
                saveRide()
            }
        }

        // 🔵 Ponowne wykonanie zdjęcia
        buttonRetakePhoto.setOnClickListener {
            if (receiptPhotoPaths.isNotEmpty()) {
                val removed = receiptPhotoPaths.removeLast()
                File(removed).delete()
            }
            updateReceiptPhotosUi()
            requestReceiptPhoto(isCardPayment())
        }

        updateReceiptPhotosUi()
        // 🔵 Obsługa zmiany źródła lub rodzaju płatności
        setupDynamicFields()

        if (intent.getBooleanExtra(EXTRA_EDIT_LAST_RIDE, false)) {
            loadLastRideForEdit()
        }
    }

    private fun setupDynamicFields() {
        fun updateFieldsVisibility() {
            val selectedSource = spinnerSource.selectedItem?.toString() ?: ""
            val selectedPayment = spinnerPaymentType.selectedItem?.toString() ?: ""

            if (selectedSource == "Dyspozytornia" && selectedPayment == "Voucher") {
                radioGroupAmountOrKm.visibility = View.VISIBLE
                if (radioAmount.isChecked) {
                    editTextAmount.visibility = View.VISIBLE
                    editTextKm.visibility = View.GONE
                } else if (radioKm.isChecked) {
                    editTextAmount.visibility = View.GONE
                    editTextKm.visibility = View.VISIBLE
                }
            } else {
                radioGroupAmountOrKm.visibility = View.GONE
                editTextAmount.visibility = View.VISIBLE
                editTextKm.visibility = View.GONE
            }
        }

        spinnerSource.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateFieldsVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerPaymentType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                updateFieldsVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        radioGroupAmountOrKm.setOnCheckedChangeListener { _, _ ->
            updateFieldsVisibility()
        }
    }

    private fun launchCamera() {
        val file = File.createTempFile("receipt_", ".jpg", getExternalFilesDir(null))
        val uri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", file)
        pendingPhotoPath = file.absolutePath
        takePictureLauncher.launch(uri)
    }

    private fun showReceiptConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_receipt_confirmation, null)
        dialogView.findViewById<ImageView>(R.id.dialogReceiptImage).apply {
            adjustViewBounds = true
            val bitmap = receiptPhotoPaths.lastOrNull()?.let { BitmapFactory.decodeFile(it) }
            setImageBitmap(bitmap)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.buttonOk).setOnClickListener {
            dialog.dismiss()
            if (!isVoucherPayment()) {
                showRememberReceiptDialog()
            }
            updateReceiptPhotosUi()
        }

        dialogView.findViewById<Button>(R.id.buttonRetry).setOnClickListener {
            dialog.dismiss()
            receiptPhotoPaths.removeLastOrNull()?.let { File(it).delete() }
            updateReceiptPhotosUi()
            requestReceiptPhoto(isCardPayment())
        }

        dialog.show()
    }

    private fun showRememberReceiptDialog() {
        val textView = TextView(this).apply {
            text = getString(R.string.remember_receipt)
            setTextColor(Color.RED)
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        AlertDialog.Builder(this)
            .setView(textView)
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .show()
    }

    private fun requestReceiptPhoto(isMandatory: Boolean) {
        val messageRes = if (isMandatory) {
            R.string.receipt_photo_prompt_mandatory
        } else {
            R.string.receipt_photo_prompt_optional
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pendingReceiptPromptRes = messageRes
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        } else {
            Toast.makeText(this, getString(messageRes), Toast.LENGTH_LONG).show()
            launchCamera()
        }
    }

    private fun isCardPayment(): Boolean {
        return spinnerPaymentType.selectedItem?.toString() == "Karta"
    }

    private fun isVoucherPayment(): Boolean {
        return spinnerPaymentType.selectedItem?.toString() == "Voucher"
    }

    private fun updateReceiptPhotosUi() {
        val count = receiptPhotoPaths.size
        textReceiptPhotosCount.text = if (count == 0) {
            "Brak zdjęć"
        } else {
            "Dodano zdjęcia: $count"
        }
        receiptPhotoPaths.lastOrNull()?.let {
            receiptPreview.setImageBitmap(BitmapFactory.decodeFile(it))
            receiptPreview.visibility = View.VISIBLE
        } ?: run {
            receiptPreview.setImageDrawable(null)
            receiptPreview.visibility = View.GONE
        }
        buttonRetakePhoto.visibility = if (count > 0) View.VISIBLE else View.GONE
    }

    private fun saveRide() {
        val driverId = SessionManager.getDriverId(this)
        if (driverId.isNullOrEmpty()) {
            Toast.makeText(this, "Brak ID kierowcy", Toast.LENGTH_SHORT).show()
            return
        }

        val source = spinnerSource.selectedItem?.toString() ?: ""
        val paymentType = spinnerPaymentType.selectedItem?.toString() ?: ""

        if (source.isEmpty() || paymentType.isEmpty()) {
            Toast.makeText(this, "Wybierz źródło i typ płatności", Toast.LENGTH_SHORT).show()
            return
        }

        var amountText = editTextAmount.text.toString()
        val kmText = editTextKm.text.toString()

        // 🔵 Jeśli Dyspozytornia + Voucher i wybrane KM
        if (source == "Dyspozytornia" && paymentType == "Voucher" && radioKm.isChecked) {
            if (kmText.isEmpty()) {
                Toast.makeText(this, "Wpisz liczbę kilometrów", Toast.LENGTH_SHORT).show()
                return
            }
            val km = kmText.toIntOrNull()
            if (km == null || km <= 0) {
                Toast.makeText(this, "Nieprawidłowa liczba kilometrów", Toast.LENGTH_SHORT).show()
                return
            }
            // Przelicznik KM → KWOTA
            amountText = when {
                km <= 6 -> "35"
                km <= 20 -> (km * 4.8f).toInt().toString()
                km <= 30 -> (km * 4.45f).toInt().toString()
                else -> (km * 4.1f).toInt().toString()
            }
        } else {
            if (amountText.isEmpty()) {
                Toast.makeText(this, "Wpisz kwotę za kurs", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // 🔵 Wysyłka kursu do API
        val viaKm = if (source == "Dyspozytornia" && paymentType == "Voucher" && radioKm.isChecked) 1 else 0

        val driverIdBody = driverId.toRequestBody("text/plain".toMediaTypeOrNull())
        val amountBody = amountText.toRequestBody("text/plain".toMediaTypeOrNull())
        val typeBody = paymentType.toRequestBody("text/plain".toMediaTypeOrNull())
        val sourceBody = source.toRequestBody("text/plain".toMediaTypeOrNull())
        val viaKmBody = viaKm.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val receiptParts = mutableListOf<MultipartBody.Part>()
        receiptPhotoPaths.forEach { path ->
            val file = File(path)
            if (file.exists()) {
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                receiptParts.add(MultipartBody.Part.createFormData("receipts[]", file.name, requestFile))
            }
        }


        if (isEditMode) {
            val call = ApiClient.apiService.updateLastRide(
                driverId = driverId,
                amount = amountText,
                type = paymentType,
                source = source,
                viaKm = viaKm
            )

            call.enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@AddRideActivity, response.body()?.message ?: "Zaktualizowano kurs", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddRideActivity, "Błąd: ${response.body()?.message ?: "Nieznany błąd"}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    this@AddRideActivity.showConnectionIssueToast(t)
                }
            })
        } else {
            val call = ApiClient.apiService.addRide(
                receiptParts,
                driverIdBody,
                amountBody,
                typeBody,
                sourceBody,
                viaKmBody
            )

            call.enqueue(object : Callback<AddRideResponse> {
                override fun onResponse(call: Call<AddRideResponse>, response: Response<AddRideResponse>) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@AddRideActivity, response.body()?.message ?: "Dodano kurs", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddRideActivity, "Błąd: ${response.body()?.message ?: "Nieznany błąd"}", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AddRideResponse>, t: Throwable) {
                    this@AddRideActivity.showConnectionIssueToast(t)
                }
            })
        }
    }

    private fun loadLastRideForEdit() {
        val driverId = SessionManager.getDriverId(this)
        if (driverId.isNullOrEmpty()) {
            Toast.makeText(this, "Brak ID kierowcy", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.apiService.getLastRide(driverId).enqueue(object : Callback<LastRideResponse> {
            override fun onResponse(call: Call<LastRideResponse>, response: Response<LastRideResponse>) {
                if (!response.isSuccessful || response.body()?.status != "success" || response.body()?.data == null) {
                    Toast.makeText(this@AddRideActivity, response.body()?.message ?: "Nie znaleziono kursu do edycji", Toast.LENGTH_LONG).show()
                    return
                }

                val ride = response.body()!!.data!!
                isEditMode = true
                buttonAddRide.text = "Zapisz zmiany kursu"
                buttonAddReceiptPhoto.visibility = View.GONE
                buttonRetakePhoto.visibility = View.GONE
                receiptPreview.visibility = View.GONE
                receiptPhotoPaths.clear()
                updateReceiptPhotosUi()

                setSpinnerSelectionByValue(spinnerSource, ride.source)
                setSpinnerSelectionByValue(spinnerPaymentType, ride.type)

                if (ride.via_km == 1 && ride.source == "Dyspozytornia" && ride.type == "Voucher") {
                    radioKm.isChecked = true
                    editTextKm.setText("")
                    editTextAmount.setText(ride.amount)
                } else {
                    radioAmount.isChecked = true
                    editTextAmount.setText(ride.amount)
                    editTextKm.setText("")
                }

                Toast.makeText(this@AddRideActivity, "Załadowano ostatni kurs do edycji", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<LastRideResponse>, t: Throwable) {
                this@AddRideActivity.showConnectionIssueToast(t)
            }
        })
    }

    private fun setSpinnerSelectionByValue(spinner: Spinner, value: String) {
        val adapter = spinner.adapter ?: return
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString() == value) {
                spinner.setSelection(i)
                return
            }
        }
    }
}
