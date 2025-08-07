package com.partner.taxi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.partner.taxi.GenericResponse
import java.io.File
import java.io.FileOutputStream

class InventoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "InventoryActivity"
    }

    private val RC_CAMERA = 1000
    private var pendingReqCode = 0

    private val REQ_FRONT  = 1
    private val REQ_BACK   = 2
    private val REQ_LEFT   = 3
    private val REQ_RIGHT  = 4
    private val REQ_DIRT1  = 5
    private val REQ_DIRT2  = 6
    private val REQ_DIRT3  = 7
    private val REQ_DIRT4  = 8

    // widoki
    private lateinit var radioCleanYes: RadioButton
    private lateinit var radioCleanNo: RadioButton
    private lateinit var dirtPhotoSection: LinearLayout

    private lateinit var btnPhotoDirt1: Button
    private lateinit var btnPhotoDirt2: Button
    private lateinit var btnPhotoDirt3: Button
    private lateinit var btnPhotoDirt4: Button
    private lateinit var imgPhotoDirt1: ImageView
    private lateinit var imgPhotoDirt2: ImageView
    private lateinit var imgPhotoDirt3: ImageView
    private lateinit var imgPhotoDirt4: ImageView

    private lateinit var btnPhotoFront: Button
    private lateinit var btnPhotoBack: Button
    private lateinit var btnPhotoLeft: Button
    private lateinit var btnPhotoRight: Button
    private lateinit var imgFrontPreview: ImageView
    private lateinit var imgBackPreview: ImageView
    private lateinit var imgLeftPreview: ImageView
    private lateinit var imgRightPreview: ImageView

    private lateinit var checkboxLicencja: CheckBox
    private lateinit var checkboxLegalizacja: CheckBox
    private lateinit var checkboxDowod: CheckBox
    private lateinit var checkboxUbezpieczenie: CheckBox
    private lateinit var checkboxKartaLotniskowa: CheckBox
    private lateinit var checkboxGasnica: CheckBox
    private lateinit var checkboxLewarek: CheckBox
    private lateinit var checkboxTrojkat: CheckBox
    private lateinit var checkboxKamizelka: CheckBox
    private lateinit var edittextVestCount: EditText

    private lateinit var radioNotesYes: RadioButton
    private lateinit var radioNotesNo: RadioButton
    private lateinit var editTextNotes: EditText

    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    // pliki zdjęć
    private var frontFile: File? = null
    private var backFile:  File? = null
    private var leftFile:  File? = null
    private var rightFile: File? = null
    private var dirtFile1: File? = null
    private var dirtFile2: File? = null
    private var dirtFile3: File? = null
    private var dirtFile4: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        // bind views
        radioCleanYes    = findViewById(R.id.radioCleanYes)
        radioCleanNo     = findViewById(R.id.radioCleanNo)
        dirtPhotoSection = findViewById(R.id.dirtPhotoSection)

        btnPhotoDirt1    = findViewById(R.id.btnPhotoDirt1)
        btnPhotoDirt2    = findViewById(R.id.btnPhotoDirt2)
        btnPhotoDirt3    = findViewById(R.id.btnPhotoDirt3)
        btnPhotoDirt4    = findViewById(R.id.btnPhotoDirt4)
        imgPhotoDirt1    = findViewById(R.id.imgPhotoDirt1)
        imgPhotoDirt2    = findViewById(R.id.imgPhotoDirt2)
        imgPhotoDirt3    = findViewById(R.id.imgPhotoDirt3)
        imgPhotoDirt4    = findViewById(R.id.imgPhotoDirt4)

        btnPhotoFront    = findViewById(R.id.btnPhotoFront)
        btnPhotoBack     = findViewById(R.id.btnPhotoBack)
        btnPhotoLeft     = findViewById(R.id.btnPhotoLeft)
        btnPhotoRight    = findViewById(R.id.btnPhotoRight)
        imgFrontPreview  = findViewById(R.id.imgFrontPreview)
        imgBackPreview   = findViewById(R.id.imgBackPreview)
        imgLeftPreview   = findViewById(R.id.imgLeftPreview)
        imgRightPreview  = findViewById(R.id.imgRightPreview)

        checkboxLicencja        = findViewById(R.id.checkboxLicencja)
        checkboxLegalizacja     = findViewById(R.id.checkboxLegalizacja)
        checkboxDowod           = findViewById(R.id.checkboxDowod)
        checkboxUbezpieczenie   = findViewById(R.id.checkboxUbezpieczenie)
        checkboxKartaLotniskowa = findViewById(R.id.checkboxKartaLotniskowa)
        checkboxGasnica         = findViewById(R.id.checkboxGasnica)
        checkboxLewarek         = findViewById(R.id.checkboxLewarek)
        checkboxTrojkat         = findViewById(R.id.checkboxTrojkat)
        checkboxKamizelka       = findViewById(R.id.checkboxKamizelka)
        edittextVestCount       = findViewById(R.id.edittextVestCount)

        radioNotesYes   = findViewById(R.id.radioNotesYes)
        radioNotesNo    = findViewById(R.id.radioNotesNo)
        editTextNotes   = findViewById(R.id.editTextNotes)

        btnSubmit       = findViewById(R.id.btnSubmitInventory)
        progressBar     = findViewById(R.id.progressBar)

        // listeners
        radioCleanYes.setOnCheckedChangeListener { _, checked ->
            dirtPhotoSection.visibility = if (checked) View.GONE else View.VISIBLE
        }
        radioCleanNo.setOnCheckedChangeListener  { _, _ ->
            dirtPhotoSection.visibility = View.VISIBLE
        }

        checkboxKamizelka.setOnCheckedChangeListener { _, checked ->
            edittextVestCount.visibility = if (checked) View.VISIBLE else View.GONE
        }

        radioNotesYes.setOnCheckedChangeListener { _, checked ->
            editTextNotes.visibility = if (checked) View.VISIBLE else View.GONE
        }
        radioNotesNo.setOnCheckedChangeListener  { _, _ ->
            editTextNotes.visibility = View.GONE
        }

        btnPhotoFront.setOnClickListener  { launchCamera(REQ_FRONT) }
        btnPhotoBack.setOnClickListener   { launchCamera(REQ_BACK) }
        btnPhotoLeft.setOnClickListener   { launchCamera(REQ_LEFT) }
        btnPhotoRight.setOnClickListener  { launchCamera(REQ_RIGHT) }

        btnPhotoDirt1.setOnClickListener  { launchCamera(REQ_DIRT1) }
        btnPhotoDirt2.setOnClickListener  { launchCamera(REQ_DIRT2) }
        btnPhotoDirt3.setOnClickListener  { launchCamera(REQ_DIRT3) }
        btnPhotoDirt4.setOnClickListener  { launchCamera(REQ_DIRT4) }

        btnSubmit.setOnClickListener { validateAndSubmit() }
    }

    private fun launchCamera(reqCode: Int) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            pendingReqCode = reqCode
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), RC_CAMERA
            )
            return
        }
        val prefix = when (reqCode) {
            REQ_FRONT  -> "front_"
            REQ_BACK   -> "back_"
            REQ_LEFT   -> "left_"
            REQ_RIGHT  -> "right_"
            else       -> "dirt_$reqCode"
        }
        val file = File.createTempFile(prefix, ".jpg", getExternalFilesDir(null))
        val uri  = FileProvider.getUriForFile(
            this, "$packageName.provider", file
        )
        when (reqCode) {
            REQ_FRONT -> frontFile = file
            REQ_BACK  -> backFile  = file
            REQ_LEFT  -> leftFile  = file
            REQ_RIGHT -> rightFile = file
            REQ_DIRT1 -> dirtFile1 = file
            REQ_DIRT2 -> dirtFile2 = file
            REQ_DIRT3 -> dirtFile3 = file
            REQ_DIRT4 -> dirtFile4 = file
        }
        pendingReqCode = reqCode
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri),
            reqCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_CAMERA
            && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera(pendingReqCode)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        fun show(iv: ImageView, f: File?) {
            f?.let {
                iv.setImageBitmap(BitmapFactory.decodeFile(it.absolutePath))
                iv.visibility = View.VISIBLE
            }
        }
        when (requestCode) {
            REQ_FRONT  -> show(imgFrontPreview, frontFile)
            REQ_BACK   -> show(imgBackPreview,  backFile)
            REQ_LEFT   -> show(imgLeftPreview,  leftFile)
            REQ_RIGHT  -> show(imgRightPreview, rightFile)
            REQ_DIRT1  -> show(imgPhotoDirt1,   dirtFile1)
            REQ_DIRT2  -> show(imgPhotoDirt2,   dirtFile2)
            REQ_DIRT3  -> show(imgPhotoDirt3,   dirtFile3)
            REQ_DIRT4  -> show(imgPhotoDirt4,   dirtFile4)
        }
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
        val bmp    = BitmapFactory.decodeFile(it.absolutePath)
        val scaled = scaleBitmap(bmp, 1024)
        val out    = File(cacheDir, "comp_${it.name}")
        FileOutputStream(out).use { fos ->
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }
        out
    }

    private fun validateAndSubmit() {
        Log.d(TAG, ">>> validateAndSubmit() start")
        btnSubmit.isEnabled    = false
        progressBar.visibility = View.VISIBLE

        // 1) obowiązkowe 4 zdjęcia pojazdu
        if (frontFile==null||backFile==null||leftFile==null||rightFile==null) {
            Toast.makeText(this,
                "Zrób zdjęcia przodu, tyłu, lewej i prawej strony",
                Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            btnSubmit.isEnabled    = true
            return
        }
        // 2) gdy zabrudzenia → min. 1 zdjęcie
        if (radioCleanNo.isChecked &&
            listOf(dirtFile1,dirtFile2,dirtFile3,dirtFile4)
                .all { it==null }
        ) {
            Toast.makeText(this,
                "Zrób przynajmniej jedno zdjęcie zabrudzenia",
                Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            btnSubmit.isEnabled    = true
            return
        }
        // 3) ilość kamizelek
        if (checkboxKamizelka.isChecked &&
            (edittextVestCount.text.toString().toIntOrNull()?:0) < 1
        ) {
            Toast.makeText(this,
                "Podaj ilość kamizelek ≥1",
                Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            btnSubmit.isEnabled    = true
            return
        }
        // 4) uwagi
        if (radioNotesYes.isChecked &&
            editTextNotes.text.isBlank()
        ) {
            Toast.makeText(this,
                "Wpisz swoje uwagi",
                Toast.LENGTH_LONG).show()
            progressBar.visibility = View.GONE
            btnSubmit.isEnabled    = true
            return
        }

        // przygotowanie RequestBody
        fun strPart(v:String) =
            RequestBody.create("text/plain".toMediaTypeOrNull(), v)
        val rePart   = strPart(intent.getStringExtra("rejestracja") ?: "")
        val prPart   = strPart(intent.getIntExtra("przebieg", 0).toString())
        val czPart   = strPart(if (radioCleanYes.isChecked) "1" else "0")
        val vestPart = strPart(
            if (checkboxKamizelka.isChecked)
                edittextVestCount.text.toString() else "0"
        )
        val licPart = strPart(if (checkboxLicencja.isChecked) "1" else "0")
        val legPart = strPart(if (checkboxLegalizacja.isChecked) "1" else "0")
        val dowPart = strPart(if (checkboxDowod.isChecked) "1" else "0")
        val ubePart = strPart(if (checkboxUbezpieczenie.isChecked) "1" else "0")
        val karPart = strPart(if (checkboxKartaLotniskowa.isChecked) "1" else "0")
        val gasPart = strPart(if (checkboxGasnica.isChecked) "1" else "0")
        val lewPart = strPart(if (checkboxLewarek.isChecked) "1" else "0")
        val troPart = strPart(if (checkboxTrojkat.isChecked) "1" else "0")
        val kamPart = strPart(if (checkboxKamizelka.isChecked) "1" else "0")
        val uwPart  = if (radioNotesYes.isChecked)
            strPart(editTextNotes.text.toString())
        else strPart("")

        // kompresja zdjęć
        val upF  = compressFile(frontFile)
        val upB  = compressFile(backFile)
        val upL  = compressFile(leftFile)
        val upR  = compressFile(rightFile)
        val upD1 = compressFile(dirtFile1)
        val upD2 = compressFile(dirtFile2)
        val upD3 = compressFile(dirtFile3)
        val upD4 = compressFile(dirtFile4)

        // helper do plików
        fun filePart(field:String, f: File?) = f?.let {
            val rb = RequestBody.create("image/jpeg".toMediaTypeOrNull(), it)
            MultipartBody.Part.createFormData(field, it.name, rb)
        }

        val pf  = filePart("photo_front",  upF)
        val pb  = filePart("photo_back",   upB)
        val pl  = filePart("photo_left",   upL)
        val pr  = filePart("photo_right",  upR)
        val pd1 = filePart("photo_dirt1",  upD1)
        val pd2 = filePart("photo_dirt2",  upD2)
        val pd3 = filePart("photo_dirt3",  upD3)
        val pd4 = filePart("photo_dirt4",  upD4)

        Log.d(TAG,"Enqueue API call")
        ApiClient.apiService.addInventory(
            rePart, prPart, czPart,
            pf, pb, pl, pr,
            pd1, pd2, pd3, pd4,
            vestPart,
            licPart, legPart, dowPart, ubePart,
            karPart, gasPart, lewPart, troPart,
            kamPart, uwPart
        ).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(
                call: Call<GenericResponse>,
                response: Response<GenericResponse>
            ) {
                Log.d(TAG, "onResponse: code=${response.code()} body=${response.body()}")
                progressBar.visibility = View.GONE
                btnSubmit.isEnabled    = true
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(
                        this@InventoryActivity,
                        "Inwentaryzacja zapisana",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(
                        this@InventoryActivity,
                        DashboardActivity::class.java
                    ).apply {
                        putExtra("rejestracja", intent.getStringExtra("rejestracja"))
                    })
                    finish()
                } else {
                    Toast.makeText(
                        this@InventoryActivity,
                        "Błąd zapisu: ${response.body()?.message ?: response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                Log.e(TAG, "onFailure", t)
                progressBar.visibility = View.GONE
                btnSubmit.isEnabled    = true
                Toast.makeText(
                    this@InventoryActivity,
                    "Błąd sieci: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
