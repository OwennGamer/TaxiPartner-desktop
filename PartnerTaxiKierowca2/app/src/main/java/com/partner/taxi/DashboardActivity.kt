package com.partner.taxi

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.partner.taxi.VehicleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.util.Locale

class DashboardActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "DashboardActivity"
    }
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tvLabelLicznik: TextView
    private lateinit var tvLabelLicznikSubtitle: TextView
    private lateinit var tvLicznik: TextView
    private lateinit var tvVoucherCurrentLabel: TextView
    private lateinit var tvVoucherCurrentValue: TextView
    private lateinit var tvVoucherPreviousLabel: TextView
    private lateinit var tvVoucherPreviousValue: TextView
    private lateinit var btnDodajKurs: MaterialButton
    private lateinit var btnHistoria: MaterialButton
    private lateinit var btnTankowanie: MaterialButton
    private lateinit var btnGrafik: MaterialButton
    private lateinit var btnFlota: MaterialButton
    private lateinit var btnChangeSaldo: MaterialButton
    private lateinit var btnPusty2: MaterialButton
    private lateinit var btnPusty3: MaterialButton
    private lateinit var btnZakonczPrace: MaterialButton
    private var vehiclePlate: String? = null
    private var restoreLockTaskAfterNavigation = false
    private var suppressNextUserLeaveHint = false

    private val changeSaldoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadDriverSaldo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_dashboard)

        vehiclePlate = intent.getStringExtra("rejestracja")
            ?: SessionManager.getVehiclePlate(this)
        if (intent.hasExtra("rejestracja")) {
            vehiclePlate?.let { SessionManager.saveVehiclePlate(this, it) }
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        tvLabelLicznik     = findViewById(R.id.tvLabelLicznik)
        tvLabelLicznikSubtitle = findViewById(R.id.tvLabelLicznikSubtitle)
        tvLicznik          = findViewById(R.id.tvLicznik)
        tvVoucherCurrentLabel = findViewById(R.id.tvVoucherCurrentLabel)
        tvVoucherCurrentValue = findViewById(R.id.tvVoucherCurrentValue)
        tvVoucherPreviousLabel = findViewById(R.id.tvVoucherPreviousLabel)
        tvVoucherPreviousValue = findViewById(R.id.tvVoucherPreviousValue)

        resetVoucherViews()
        btnDodajKurs       = findViewById(R.id.btnDodajKurs)
        btnHistoria        = findViewById(R.id.btnHistoria)
        btnTankowanie      = findViewById(R.id.btnTankowanie)
        btnGrafik          = findViewById(R.id.btnGrafik)
        btnFlota           = findViewById(R.id.btnFlota)
        btnChangeSaldo     = findViewById(R.id.btnChangeSaldo)
        btnPusty2          = findViewById(R.id.btnPusty2)
        btnPusty3          = findViewById(R.id.btnPusty3)
        btnZakonczPrace    = findViewById(R.id.btnZakonczPrace)

        val role = SessionManager.getRole(this).lowercase()
        val isAdmin = role == "administrator"
        val canAccessFleet = role == "flotowiec" || isAdmin

        btnChangeSaldo.visibility = if (isAdmin) View.VISIBLE else View.GONE
        if (isAdmin) {
            btnChangeSaldo.setOnClickListener {
                val intent = Intent(this, ChangeSaldoActivity::class.java).apply {
                    putExtra(
                        ChangeSaldoActivity.EXTRA_DRIVER_ID,
                        SessionManager.getDriverId(this@DashboardActivity)
                    )
                }
                launchActivityForResultHandlingLockTask(changeSaldoLauncher, intent)
            }
        } else {
            btnChangeSaldo.setOnClickListener(null)
        }

        btnFlota.setOnClickListener {
            if (!canAccessFleet) {
                Toast.makeText(this, "BRAK UPRAWNIEŃ", Toast.LENGTH_SHORT).show()
            } else {
                startActivityHandlingLockTask(Intent(this, FleetActivity::class.java))
            }
        }

        btnFlota.alpha = if (canAccessFleet) 1f else 0.5f



        // <-- PRZYWRACAMY LOSOWE KOLORY DLA KAFELKÓW -->
        val buttons = listOf(
            btnDodajKurs, btnHistoria, btnTankowanie, btnGrafik,
            btnFlota, btnChangeSaldo, btnPusty2, btnPusty3
        )
        val colors = listOf(
            "#F44336", "#E91E63", "#9C27B0", "#673AB7",
            "#3F51B5", "#03A9F4", "#009688", "#8BC34A"
        ).shuffled()
        buttons.forEachIndexed { i, btn ->
            btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colors[i]))
        }
        // <-- KONIEC FRAGMENTU Z KOLORAMI -->

        // Listener do dodawania kursu
        btnDodajKurs.setOnClickListener {
            Log.d(TAG, "Kliknięto 'Dodaj kurs'")
            startActivityHandlingLockTask(Intent(this, AddRideActivity::class.java))
        }

        // Listener do historii
        btnHistoria.setOnClickListener {
            Log.d(TAG, "Kliknięto 'Historia'")
            startActivityHandlingLockTask(Intent(this, HistoryActivity::class.java))
        }

        // Listener do tankowania
        btnTankowanie.setOnClickListener {
            Log.d(TAG, "Kliknięto 'Tankowanie'")
            startActivityHandlingLockTask(Intent(this, FuelActivity::class.java))
        }

        // Listener do zakończenia pracy
        btnZakonczPrace.setOnClickListener {
            val sessionId = SessionManager.getSessionId(this)
            if (sessionId.isNullOrEmpty()) {
                Toast.makeText(this, "Brak aktywnej sesji", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val input = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
            }

            AlertDialog.Builder(this)
                .setTitle("Podaj przebieg")
                .setView(input)
                .setPositiveButton("OK") { _, _ ->
                    val odo = input.text.toString().toIntOrNull()
                    if (odo == null) {
                        Toast.makeText(this, "Nieprawidłowa wartość", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    validateAndEndShift(odo)
                }
                .setNegativeButton("Anuluj", null)
                .show()
        }

        swipeRefreshLayout.setOnRefreshListener {
            loadDriverSaldo { swipeRefreshLayout.isRefreshing = false }
        }

        // Wczytaj saldo od razu
        loadDriverSaldo()

        lockTaskIfSessionActive()
    }

    override fun onResume() {
        super.onResume()
        loadDriverSaldo()
        if (restoreLockTaskAfterNavigation || !isLockTaskActive()) {
            lockTaskIfSessionActive()
        }
        restoreLockTaskAfterNavigation = false
    }

    private fun startActivityHandlingLockTask(intent: Intent) {
        val lockTaskActive = isLockTaskActive()
        Log.d(TAG, "startActivityHandlingLockTask: lockTaskActive=$lockTaskActive intent=${intent.component}")

        if (lockTaskActive) {
            restoreLockTaskAfterNavigation = true

            val devicePolicyManager =
                getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val lockTaskPermitted =
                devicePolicyManager?.isLockTaskPermitted(packageName) == true

            Log.d(TAG, "lockTaskPermitted=$lockTaskPermitted")

            if (lockTaskPermitted) {
                // Jeżeli aplikacja ma pełne uprawnienia kioskowe – zwykłe zatrzymanie lock task
                runCatching { stopLockTask() }
                    .onFailure { error ->
                        Log.w(TAG, "stopLockTask() nie powiodło się mimo uprawnień", error)
                    }
            } else {
                // W przypadku ręcznego pinowania ekranu spróbuj wyjść z trybu, a brak uprawnień po prostu ignoruj
                try {
                    stopLockTask()
                } catch (_: SecurityException) {
                    // Użytkownik musi ręcznie zdjąć pin – przechodzimy dalej, aby nie blokować nawigacji
                    Log.i(TAG, "Brak uprawnień do wyjścia z trybu pinowania – kontynuujemy nawigację")
                }
            }
        }
        suppressNextUserLeaveHint = true
        runCatching {
            startActivity(intent)
        }.onFailure { error ->
            suppressNextUserLeaveHint = false
            Log.e(TAG, "startActivity(${intent.component}) zakończone błędem", error)
        }
    }

    private fun launchActivityForResultHandlingLockTask(
        launcher: ActivityResultLauncher<Intent>,
        intent: Intent
    ) {
        val lockTaskActive = isLockTaskActive()
        Log.d(TAG, "launchActivityForResultHandlingLockTask: lockTaskActive=$lockTaskActive intent=${intent.component}")

        if (lockTaskActive) {
            restoreLockTaskAfterNavigation = true

            val devicePolicyManager =
                getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val lockTaskPermitted =
                devicePolicyManager?.isLockTaskPermitted(packageName) == true

            if (lockTaskPermitted) {
                runCatching { stopLockTask() }
                    .onFailure { error ->
                        Log.w(TAG, "stopLockTask() nie powiodło się mimo uprawnień", error)
                    }
            } else {
                try {
                    stopLockTask()
                } catch (_: SecurityException) {
                    Log.i(TAG, "Brak uprawnień do wyjścia z trybu pinowania – kontynuujemy nawigację")
                }
            }
        }

        suppressNextUserLeaveHint = true
        runCatching {
            launcher.launch(intent)
        }.onFailure { error ->
            suppressNextUserLeaveHint = false
            Log.e(
                TAG,
                "launchActivityForResultHandlingLockTask(${intent.component}) zakończone błędem",
                error
            )
        }
    }

    private fun isLockTaskActive(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val lockTaskState = activityManager?.lockTaskModeState ?: ActivityManager.LOCK_TASK_MODE_NONE
        return lockTaskState != ActivityManager.LOCK_TASK_MODE_NONE
    }

    private fun lockTaskIfSessionActive() {
        val sessionActive = !SessionManager.getSessionId(this).isNullOrEmpty()
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val lockTaskState = activityManager?.lockTaskModeState ?: ActivityManager.LOCK_TASK_MODE_NONE

        if (!sessionActive) {
            if (lockTaskState != ActivityManager.LOCK_TASK_MODE_NONE) {
                runCatching { stopLockTask() }
            }
            return
        }

        val devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
        val lockTaskPermitted = devicePolicyManager?.isLockTaskPermitted(packageName) == true

        if (!lockTaskPermitted && lockTaskState != ActivityManager.LOCK_TASK_MODE_NONE) {
            runCatching { stopLockTask() }
            return
        }

        if (lockTaskPermitted && lockTaskState == ActivityManager.LOCK_TASK_MODE_NONE) {
            runCatching { startLockTask() }
        }
    }

    private fun loadDriverSaldo(onComplete: (() -> Unit)? = null) {
        val driverId = SessionManager.getDriverId(this)
        if (driverId.isNullOrEmpty()) {
            tvLicznik.text = "0.00 zł"
            onComplete?.invoke()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getDriverData(driverId)
                val body = response.body()
                if (response.isSuccessful && body?.status == "success" && body.data != null) {
                    val data = body.data
                    updateSaldoViews(data)
                } else {
                    tvLicznik.text = "0.00 zł"
                    resetVoucherViews()
                    Toast.makeText(this@DashboardActivity, "Błąd pobierania salda", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                tvLicznik.text = "0.00 zł"
                resetVoucherViews()
                this@DashboardActivity.showConnectionIssueToast(e)
            } finally {
                onComplete?.invoke()
            }
        }
    }

    private fun updateSaldoViews(data: DriverData) {
        val locale = Locale("pl", "PL")
        tvLabelLicznik.text = getString(R.string.label_saldo)
        tvLabelLicznikSubtitle.text = getString(R.string.label_without_vouchers)
        tvLicznik.text = formatCurrency(data.saldo, locale)

        val currentMonthLabel = monthDisplayName(data.voucherCurrentMonth, locale)
        val previousMonthLabel = monthDisplayName(data.voucherPreviousMonth, locale)

        tvVoucherCurrentLabel.text = getString(R.string.label_vouchers_month, currentMonthLabel ?: getString(R.string.label_month_current))
        tvVoucherPreviousLabel.text = getString(R.string.label_vouchers_month, previousMonthLabel ?: getString(R.string.label_month_previous))

        tvVoucherCurrentValue.text = formatCurrency(data.voucherCurrentAmount, locale)
        tvVoucherPreviousValue.text = formatCurrency(data.voucherPreviousAmount, locale)
    }

    private fun resetVoucherViews() {
        val locale = Locale("pl", "PL")
        tvLabelLicznik.text = getString(R.string.label_saldo)
        tvLabelLicznikSubtitle.text = getString(R.string.label_without_vouchers)
        tvLicznik.text = formatCurrency(0f, locale)
        tvVoucherCurrentLabel.text = getString(R.string.label_vouchers_month, getString(R.string.label_month_current))
        tvVoucherPreviousLabel.text = getString(R.string.label_vouchers_month, getString(R.string.label_month_previous))
        tvVoucherCurrentValue.text = formatCurrency(0f, locale)
        tvVoucherPreviousValue.text = formatCurrency(0f, locale)
    }

    private fun monthDisplayName(monthKey: String?, locale: Locale): String? {
        if (monthKey.isNullOrBlank()) return null
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
            val ym = YearMonth.parse(monthKey, formatter)
            val monthName = ym.month.getDisplayName(TextStyle.FULL, locale)
            monthName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        } catch (ex: DateTimeParseException) {
            null
        }
    }

    private fun formatCurrency(value: Float, locale: Locale): String = String.format(locale, "%.2f zł", value)

    private fun endShiftAndUpdate(odometer: Int, plate: String) {
        ApiClient.apiService.endShift(odometer)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        SessionManager.clearSessionId(this@DashboardActivity)
                        SessionManager.clearVehiclePlate(this@DashboardActivity)
                        Toast.makeText(
                            this@DashboardActivity,
                            response.body()?.message ?: "Zakończono pracę",
                            Toast.LENGTH_SHORT
                        ).show()
                        ApiClient.apiService.updateVehicleMileage(plate, odometer)
                            .enqueue(object : Callback<GenericResponse> {
                                override fun onResponse(
                                    call: Call<GenericResponse>,
                                    resp: Response<GenericResponse>
                                ) { /* brak akcji */ }

                                override fun onFailure(call: Call<GenericResponse>, t: Throwable) { }
                            })
                        runCatching { stopLockTask() }
                        startActivity(Intent(this@DashboardActivity, ChooseVehicleActivity::class.java))
                        finish()
                    } else {
                        val body = response.body()
                        val msg = body?.message ?: "Błąd zakończenia"
                        Toast.makeText(this@DashboardActivity, msg, Toast.LENGTH_LONG).show()
                        RemoteLogService.logWarning(
                            summary = "Nieudane zakończenie zmiany",
                            details = "status=${body?.status ?: "brak"} podczas endShift",
                            metadata = mapOf(
                                "http_code" to response.code(),
                                "status" to body?.status,
                                "message" to body?.message
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    this@DashboardActivity.showConnectionIssueToast(t)
                }
            })
    }


    private fun validateAndEndShift(odometer: Int) {
        val plate = vehiclePlate
        if (plate.isNullOrEmpty()) {
            Toast.makeText(this, "Brak informacji o pojeździe", Toast.LENGTH_SHORT).show()
            return
        }
        ApiClient.apiService.getVehicleInfo(plate)
            .enqueue(object : Callback<VehicleResponse> {
                override fun onResponse(
                    call: Call<VehicleResponse>,
                    response: Response<VehicleResponse>
                ) {
                    val current = response.body()?.data?.przebieg
                    if (!response.isSuccessful || current == null) {
                        Toast.makeText(this@DashboardActivity, "Błąd danych pojazdu", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (odometer < current) {
                        Toast.makeText(
                            this@DashboardActivity,
                            "Przebieg nie może być mniejszy niż $current",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    endShiftAndUpdate(odometer, plate)
                }

                override fun onFailure(call: Call<VehicleResponse>, t: Throwable) {
                    this@DashboardActivity.showConnectionIssueToast(t)
                }
            })
    }

    override fun onBackPressed() {
        if (!SessionManager.getSessionId(this).isNullOrEmpty()) {
            Toast.makeText(this, "Użyj przycisku 'Zakończ pracę'", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (suppressNextUserLeaveHint) {
            suppressNextUserLeaveHint = false
            return
        }
        if (!SessionManager.getSessionId(this).isNullOrEmpty()) {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }
}
