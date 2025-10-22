package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aplicativo_text_10.R
import com.aplicativo_text_10.data.AppDatabase
import kotlinx.coroutines.launch

class CheckVehicleActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var cpfAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_vehicle)

        db = AppDatabase.getDatabase(applicationContext)

        val etLicenseCpf = findViewById<AutoCompleteTextView>(R.id.etLicenseCpf)
        val etLicensePlate = findViewById<EditText>(R.id.etLicensePlate)
        val btnCheck = findViewById<Button>(R.id.btnCheckVehicle)
        val btnRegister = findViewById<Button>(R.id.btnRegisterNewVehicle)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnBack = findViewById<ImageButton>(R.id.imageViewBack)

        btnBack.setOnClickListener { finish() }

        // Adapter inicial vazio
        cpfAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        etLicenseCpf.setAdapter(cpfAdapter)
        etLicenseCpf.threshold = 1 // Começa a sugerir a partir do primeiro caractere

        // Atualiza lista de CPFs do banco enquanto digita
        etLicenseCpf.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString()?.replace(Regex("[^\\d]"), "") ?: ""
                if (input.isEmpty()) return
                lifecycleScope.launch {
                    val cpfs = db.vehicleDao().getCpfsStartingWith(input)
                    runOnUiThread {
                        cpfAdapter.clear()
                        cpfAdapter.addAll(cpfs)
                        cpfAdapter.notifyDataSetChanged()
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Máscara CPF (111.111.111-11)
        etLicenseCpf.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "###.###.###-##"
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) { isUpdating = false; return }
                val digits = (s ?: "").toString().replace(Regex("[^\\d]"), "")
                var formatted = ""
                var i = 0
                for (m in mask) {
                    if (m != '#' && digits.length > i) {
                        formatted += m
                    } else {
                        if (i < digits.length) {
                            formatted += digits[i]
                            i++
                        } else break
                    }
                }
                isUpdating = true
                etLicenseCpf.setText(formatted)
                etLicenseCpf.setSelection(formatted.length)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Máscara Placa (AAA-1111)
        etLicensePlate.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) { isUpdating = false; return }
                val raw = (s ?: "").toString().replace("-", "").uppercase()
                val limited = raw.take(7)
                val sb = StringBuilder()
                for (i in limited.indices) {
                    sb.append(limited[i])
                    if (i == 2 && limited.length > 3) sb.append("-")
                }
                isUpdating = true
                etLicensePlate.setText(sb.toString())
                etLicensePlate.setSelection(sb.length)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnCheck.setOnClickListener {
            val cpf = etLicenseCpf.text.toString().trim()
            val placa = etLicensePlate.text.toString().trim()
            if (cpf.isEmpty() || placa.isEmpty()) {
                Toast.makeText(this, "Preencha CPF e placa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val existente = db.vehicleDao().checkVehicleByNameAndPlate(cpf, placa)
                runOnUiThread {
                    if (existente != null) {
                        tvResult.text = "Já cadastrado: ${existente.nomeCompleto}, ${existente.modeloCor}"
                        tvResult.visibility = TextView.VISIBLE
                        btnRegister.visibility = Button.GONE
                    } else {
                        tvResult.text = "Veículo não encontrado. Deseja registrar?"
                        tvResult.visibility = TextView.VISIBLE
                        btnRegister.visibility = Button.VISIBLE
                    }
                }
            }
        }

        // Enviar CPF e Placa para AddEntryActivity
        btnRegister.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            intent.putExtra("cpf", etLicenseCpf.text.toString())
            intent.putExtra("placa", etLicensePlate.text.toString())
            startActivity(intent)
        }
    }
}
