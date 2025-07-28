package com.partner.taxi

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRideActivity : AppCompatActivity() {

    private lateinit var spinnerSource: Spinner
    private lateinit var spinnerPaymentType: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var radioGroupAmountOrKm: RadioGroup
    private lateinit var radioAmount: RadioButton
    private lateinit var radioKm: RadioButton
    private lateinit var editTextKm: EditText
    private lateinit var buttonAddRide: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ride)

        spinnerSource = findViewById(R.id.spinnerSource)
        spinnerPaymentType = findViewById(R.id.spinnerPaymentType)
        editTextAmount = findViewById(R.id.editTextAmount)
        radioGroupAmountOrKm = findViewById(R.id.radioGroupAmountOrKm)
        radioAmount = findViewById(R.id.radioAmount)
        radioKm = findViewById(R.id.radioKm)
        editTextKm = findViewById(R.id.editTextKm)
        buttonAddRide = findViewById(R.id.buttonAddRide)

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
            addRide()
        }

        // 🔵 Obsługa zmiany źródła lub rodzaju płatności
        setupDynamicFields()
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

    private fun addRide() {
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
        val call = ApiClient.apiService.addRide(
            driverId,
            amountText,
            paymentType,
            source,
            viaKm
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
                Toast.makeText(this@AddRideActivity, "Błąd połączenia: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
