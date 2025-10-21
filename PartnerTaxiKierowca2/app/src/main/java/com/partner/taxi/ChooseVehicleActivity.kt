@file:SuppressLint("UnsafeOptInUsageError")
package com.partner.taxi

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import android.util.Log

class ChooseVehicleActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var formScroll: ScrollView
    private lateinit var editRejestracja: EditText
    private lateinit var editPrzebieg: EditText
    private lateinit var btnScanQr: Button
    private lateinit var btnStartWork: Button

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val scanner = BarcodeScanning.getClient()
    private var cameraProvider: ProcessCameraProvider? = null

    // Retrofit API
    private val api = ApiClient.apiService

    // Permission launcher
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCameraAndScan()
            else Toast.makeText(this, "Brak dostępu do kamery", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_choose_vehicle)

        previewView      = findViewById(R.id.previewView)
        formScroll       = findViewById(R.id.formScroll)
        editRejestracja  = findViewById(R.id.editRejestracja)
        editPrzebieg     = findViewById(R.id.editPrzebieg)
        btnScanQr        = findViewById(R.id.btnScanQr)
        btnStartWork     = findViewById(R.id.btnStartWork)

        btnScanQr.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startCameraAndScan()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        btnStartWork.setOnClickListener { startWork() }
    }

    private fun startCameraAndScan() {
        formScroll.visibility = View.GONE
        previewView.visibility = View.VISIBLE

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // Preview use-case
            val previewUseCase = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // Analysis use-case
            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        scanImageProxy(imageProxy)
                    }
                }

            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                this, CameraSelector.DEFAULT_BACK_CAMERA, previewUseCase, analysisUseCase
            )
        }, ContextCompat.getMainExecutor(this))
    }

    private fun scanImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage, imageProxy.imageInfo.rotationDegrees
            )
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull { it.rawValue != null }?.let { barcode ->
                        onBarcodeScanned(barcode.rawValue!!)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun onBarcodeScanned(value: String) {
        cameraProvider?.unbindAll()
        runOnUiThread {
            previewView.visibility = View.GONE
            formScroll.visibility = View.VISIBLE
            editRejestracja.setText(value)
        }
    }

    private fun startWork() {
        val rejestracja = editRejestracja.text.toString().trim()
        val przebiegStr = editPrzebieg.text.toString().trim()
        if (rejestracja.isEmpty() || przebiegStr.isEmpty()) {
            Toast.makeText(this, "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show()
            return
        }
        val przebieg = przebiegStr.toIntOrNull()
        if (przebieg == null) {
            Toast.makeText(this, "Nieprawidłowy przebieg", Toast.LENGTH_SHORT).show()
            return
        }

        api.getVehicleInfo(rejestracja).enqueue(object : Callback<VehicleResponse> {
            override fun onResponse(
                call: Call<VehicleResponse>,
                response: Response<VehicleResponse>
            ) {
                val vehicle = response.body()?.data
                if (!response.isSuccessful || vehicle == null) {
                    Toast.makeText(
                        this@ChooseVehicleActivity,
                        "Nie znaleziono pojazdu",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (przebieg < vehicle.przebieg) {
                    Toast.makeText(
                        this@ChooseVehicleActivity,
                        "Przebieg nie może być mniejszy niż ${vehicle.przebieg}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                api.updateVehicleMileage(rejestracja, przebieg)
                    .enqueue(object : Callback<GenericResponse> {
                        override fun onResponse(
                            call: Call<GenericResponse>,
                            resp: Response<GenericResponse>
                        ) {
                            if (resp.isSuccessful && resp.body()?.status == "success") {
                                startShiftAndContinue(vehicle.ostatni_kierowca_id, rejestracja, przebieg)
                            } else {
                                Toast.makeText(
                                    this@ChooseVehicleActivity,
                                    resp.body()?.message ?: "Błąd aktualizacji przebiegu",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                            this@ChooseVehicleActivity.showConnectionIssueToast(t)

                        }
                    })
            }
            override fun onFailure(call: Call<VehicleResponse>, t: Throwable) {
                this@ChooseVehicleActivity.showConnectionIssueToast(t)
            }
        })
    }

    private fun startShiftAndContinue(
        lastDriverId: String?,
        rejestracja: String,
        przebieg: Int
    ) {
        api.startShift(rejestracja, przebieg)
            .enqueue(object : Callback<StartShiftResponse> {
                override fun onResponse(
                    call: Call<StartShiftResponse>,
                    response: Response<StartShiftResponse>
                ) {
                    val body = response.body()
                    if (response.isSuccessful && body?.status == "success" && !body.sessionId.isNullOrBlank()) {
                        SessionManager.saveSessionId(this@ChooseVehicleActivity, body.sessionId)
                        SessionManager.saveVehiclePlate(this@ChooseVehicleActivity, rejestracja)
                        proceedAfterUpdate(lastDriverId, rejestracja, przebieg)
                    } else {
                        Toast.makeText(
                            this@ChooseVehicleActivity,
                            body?.message ?: "Błąd rozpoczęcia zmiany",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StartShiftResponse>, t: Throwable) {
                    this@ChooseVehicleActivity.showConnectionIssueToast(t)
                }
            })
    }

    private fun proceedAfterUpdate(
        lastDriverId: String?,   // wartość zwrócona z API
        rejestracja: String,
        przebieg: Int
    ) {
        // 1. Pobierz zalogowane ID kierowcy (String)
        val currentDriverId = SessionManager.getDriverId(this)

        // 2. Dla diagnostyki zapiszcie w logu oba ID:
        Log.d("CHOOSE_VEHICLE", "ostatni kierowca (API): '$lastDriverId', zalogowany: '$currentDriverId'")

        // 3. Jeśli brak poprzedniego rekordu LUB ID różne → inwentaryzacja,
        //    w przeciwnym razie od razu Dashboard.
        val nextActivity = if (lastDriverId.isNullOrBlank() || lastDriverId != currentDriverId) {
            InventoryActivity::class.java
        } else {
            DashboardActivity::class.java
        }

        startActivity(
            Intent(this, nextActivity).apply {
                putExtra("rejestracja", rejestracja)
                putExtra("przebieg", przebieg)
            }
        )
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
