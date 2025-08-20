package com.partner.taxi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VehicleDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        supportActionBar?.apply {
            title = "Szczegóły pojazdu"
            setDisplayHomeAsUpEnabled(true)
        }

        val vehicle = intent.getSerializableExtra("vehicle") as? VehicleData
        vehicle?.let { v ->
            findViewById<TextView>(R.id.tvRejestracja).text = "Rejestracja: ${v.rejestracja}"
            findViewById<TextView>(R.id.tvMarka).text = "Marka: ${v.marka ?: ""}"
            findViewById<TextView>(R.id.tvModel).text = "Model: ${v.model ?: ""}"
            findViewById<TextView>(R.id.tvPrzebieg).text = "Przebieg: ${v.przebieg}"
            findViewById<TextView>(R.id.tvUbezpieczenie).text = "Ubezpieczenie do: ${v.ubezpieczenie_do ?: ""}"
            findViewById<TextView>(R.id.tvPrzeglad).text = "Przegląd do: ${v.przeglad_do ?: ""}"
            findViewById<TextView>(R.id.tvAktywny).text = "Aktywny: ${v.aktywny}"
            findViewById<TextView>(R.id.tvOstatniKierowca).text = "Ostatni kierowca ID: ${v.ostatni_kierowca_id ?: ""}"
            findViewById<TextView>(R.id.tvInpost).text = "Inpost: ${v.inpost}"
            findViewById<TextView>(R.id.tvTaxi).text = "Taxi: ${v.taxi}"
            findViewById<TextView>(R.id.tvTaksometr).text = "Taksometr: ${v.taksometr}"
            findViewById<TextView>(R.id.tvLegalizacjaTaksometru).text = "Legalizacja taksometru do: ${v.legalizacja_taksometru_do ?: ""}"
            findViewById<TextView>(R.id.tvGaz).text = "Gaz: ${v.gaz}"
            findViewById<TextView>(R.id.tvHomologacjaLpg).text = "Homologacja LPG do: ${v.homologacja_lpg_do ?: ""}"
            findViewById<TextView>(R.id.tvFirma).text = "Firma: ${v.firma ?: ""}"
            findViewById<TextView>(R.id.tvFormaWlasnosci).text = "Forma własności: ${v.forma_wlasnosci ?: ""}"
            findViewById<TextView>(R.id.tvNumerPolisy).text = "Numer polisy: ${v.numer_polisy ?: ""}"

            findViewById<Button>(R.id.btnService).setOnClickListener {
                val intent = Intent(this, ServiceActivity::class.java)
                intent.putExtra("rejestracja", v.rejestracja)
                startActivity(intent)
            }

            findViewById<Button>(R.id.btnDamage).setOnClickListener {
                val intent = Intent(this, DamageActivity::class.java)
                intent.putExtra("rejestracja", v.rejestracja)
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}