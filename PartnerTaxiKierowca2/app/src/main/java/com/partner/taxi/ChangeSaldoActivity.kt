package com.partner.taxi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import kotlin.math.abs

class ChangeSaldoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DRIVER_ID = "driver_id"
    }

    private lateinit var layoutDriverId: TextInputLayout
    private lateinit var inputDriverId: TextInputEditText
    private lateinit var layoutSaldoAmount: TextInputLayout
    private lateinit var inputSaldoAmount: TextInputEditText
    private lateinit var layoutVoucherCurrent: TextInputLayout
    private lateinit var inputVoucherCurrent: TextInputEditText
    private lateinit var layoutVoucherPrevious: TextInputLayout
    private lateinit var inputVoucherPrevious: TextInputEditText
    private lateinit var layoutReason: TextInputLayout
    private lateinit var inputReason: AutoCompleteTextView
    private lateinit var layoutCustomReason: TextInputLayout
    private lateinit var inputCustomReason: TextInputEditText
    private lateinit var buttonSubmit: Button
    private lateinit var buttonCancel: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var otherReasonLabel: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!ensureTokenOrRedirect()) return
        setContentView(R.layout.activity_change_saldo)

        layoutDriverId = findViewById(R.id.layoutDriverId)
        inputDriverId = findViewById(R.id.inputDriverId)
        layoutSaldoAmount = findViewById(R.id.layoutSaldoAmount)
        inputSaldoAmount = findViewById(R.id.inputSaldoAmount)
        layoutVoucherCurrent = findViewById(R.id.layoutVoucherCurrent)
        inputVoucherCurrent = findViewById(R.id.inputVoucherCurrent)
        layoutVoucherPrevious = findViewById(R.id.layoutVoucherPrevious)
        inputVoucherPrevious = findViewById(R.id.inputVoucherPrevious)
        layoutReason = findViewById(R.id.layoutReason)
        inputReason = findViewById(R.id.inputReason)
        layoutCustomReason = findViewById(R.id.layoutCustomReason)
        inputCustomReason = findViewById(R.id.inputCustomReason)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        buttonCancel = findViewById(R.id.buttonCancel)
        progressBar = findViewById(R.id.progressBar)

        val defaultDriverId = intent.getStringExtra(EXTRA_DRIVER_ID)
            ?: SessionManager.getDriverId(this)
        inputDriverId.setText(defaultDriverId)

        otherReasonLabel = getString(R.string.change_saldo_reason_other)
        setupReasonDropdown()

        buttonSubmit.setOnClickListener { submitChanges() }
        buttonCancel.setOnClickListener { finish() }
    }

    private fun setupReasonDropdown() {
        val reasons = resources.getStringArray(R.array.change_saldo_reasons).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, reasons)
        inputReason.setAdapter(adapter)
        inputReason.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputReason.text.isNullOrEmpty()) {
                inputReason.showDropDown()
            }
        }
        inputReason.setOnItemClickListener { _, _, _, _ ->
            layoutReason.error = null
            updateCustomReasonVisibility()
        }
        updateCustomReasonVisibility()
    }

    private fun updateCustomReasonVisibility() {
        val shouldShow = inputReason.text?.toString() == otherReasonLabel
        layoutCustomReason.isVisible = shouldShow
        if (!shouldShow) {
            layoutCustomReason.error = null
            inputCustomReason.setText("")
        }
    }

    private fun submitChanges() {
        clearErrors()

        val driverId = inputDriverId.text?.toString()?.trim().orEmpty()
        if (driverId.isEmpty()) {
            layoutDriverId.error = getString(R.string.change_saldo_error_driver)
            return
        }

        val saldoAmount = parseAmount(inputSaldoAmount.text)
        val voucherCurrentAmount = parseAmount(inputVoucherCurrent.text)
        val voucherPreviousAmount = parseAmount(inputVoucherPrevious.text)

        var hasError = false
        if (saldoAmount == null) {
            layoutSaldoAmount.error = getString(R.string.change_saldo_error_amount_invalid)
            hasError = true
        }
        if (voucherCurrentAmount == null) {
            layoutVoucherCurrent.error = getString(R.string.change_saldo_error_amount_invalid)
            hasError = true
        }
        if (voucherPreviousAmount == null) {
            layoutVoucherPrevious.error = getString(R.string.change_saldo_error_amount_invalid)
            hasError = true
        }
        if (hasError) return

        val saldo = saldoAmount!!
        val voucherCurrent = voucherCurrentAmount!!
        val voucherPrevious = voucherPreviousAmount!!

        val hasAnyChange = listOf(saldo, voucherCurrent, voucherPrevious)
            .any { abs(it) > 1e-6 }
        if (!hasAnyChange) {
            layoutSaldoAmount.error = getString(R.string.change_saldo_error_amount_required)
            layoutVoucherCurrent.error = getString(R.string.change_saldo_error_amount_required)
            layoutVoucherPrevious.error = getString(R.string.change_saldo_error_amount_required)
            return
        }

        val reason = inputReason.text?.toString()?.trim().orEmpty()
        if (reason.isEmpty()) {
            layoutReason.error = getString(R.string.change_saldo_error_reason)
            return
        }

        var customReason: String? = null
        if (reason == otherReasonLabel) {
            customReason = inputCustomReason.text?.toString()?.trim()
            if (customReason.isNullOrEmpty()) {
                layoutCustomReason.error = getString(R.string.change_saldo_error_custom_reason)
                return
            }
        }

        val request = UpdateSaldoRequest(
            id = driverId,
            saldoAmount = saldo,
            voucherCurrentAmount = voucherCurrent,
            voucherPreviousAmount = voucherPrevious,
            reason = reason,
            customReason = customReason
        )

        lifecycleScope.launch {
            setLoading(true)
            try {
                val response = ApiClient.updateSaldo(request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.status == "success") {
                        val message = body.message ?: getString(R.string.change_saldo_success)
                        Toast.makeText(this@ChangeSaldoActivity, message, Toast.LENGTH_LONG).show()
                        setResult(RESULT_OK, Intent().apply {
                            putExtra("updated_saldo", body.newSaldo)
                        })
                        finish()
                    } else {
                        val errorMessage = body?.message ?: getString(R.string.change_saldo_error_generic)
                        Toast.makeText(this@ChangeSaldoActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(
                        this@ChangeSaldoActivity,
                        getString(R.string.change_saldo_error_http, response.code()),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (ex: Exception) {
                this@ChangeSaldoActivity.showConnectionIssueToast(ex)
            } finally {
                setLoading(false)
            }
        }
    }

    private fun parseAmount(text: CharSequence?): Double? {
        if (text.isNullOrBlank()) return 0.0
        val normalized = text.toString().replace(',', '.').trim()
        return normalized.toDoubleOrNull()
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonSubmit.isEnabled = !isLoading
        buttonCancel.isEnabled = !isLoading
        layoutDriverId.isEnabled = !isLoading
        layoutSaldoAmount.isEnabled = !isLoading
        layoutVoucherCurrent.isEnabled = !isLoading
        layoutVoucherPrevious.isEnabled = !isLoading
        layoutReason.isEnabled = !isLoading
        layoutCustomReason.isEnabled = !isLoading
        inputDriverId.isEnabled = !isLoading
        inputSaldoAmount.isEnabled = !isLoading
        inputVoucherCurrent.isEnabled = !isLoading
        inputVoucherPrevious.isEnabled = !isLoading
        inputReason.isEnabled = !isLoading
        inputCustomReason.isEnabled = !isLoading
    }

    private fun clearErrors() {
        layoutDriverId.error = null
        layoutSaldoAmount.error = null
        layoutVoucherCurrent.error = null
        layoutVoucherPrevious.error = null
        layoutReason.error = null
        layoutCustomReason.error = null
    }
}