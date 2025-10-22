package com.aplicativo_text_10.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aplicativo_text_10.data.AppDatabase
import com.aplicativo_text_10.databinding.ActivityAddEntryBinding
import com.aplicativo_text_10.model.VehicleEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.launch

class AddEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEntryBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var vehicleId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMotivoDropdown()
        setupAutoDateTime()
        setupMasks()
        setupAutoFillFromIntent()
        setupModeloCorMask()
        setupListeners()
    }

    // Data e hora de entrada automáticas
    private fun setupAutoDateTime() {
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        binding.editTextData.setText(sdfDate.format(calendar.time))
        binding.editTextHoraEntrada.setText(sdfTime.format(calendar.time))
    }

    // Recebe dados do CheckVehicleActivity ou edição
    private fun setupAutoFillFromIntent() {
        vehicleId = intent.getIntExtra("vehicleId", 0).takeIf { it != 0 }

        val cpfExtra = intent.getStringExtra("cpf").orEmpty()
        val placaExtra = intent.getStringExtra("placa").orEmpty()
        val nomeExtra = intent.getStringExtra("nome").orEmpty()
        // Aceita tanto "modelo" quanto "modeloCor" do Intent
        val modeloExtra = intent.getStringExtra("modelo") ?: intent.getStringExtra("modeloCor") ?: ""
        val motivoExtra = intent.getStringExtra("motivo").orEmpty()
        val dataExtra = intent.getStringExtra("data").orEmpty()
        val horaEntradaExtra = intent.getStringExtra("horaEntrada").orEmpty()
        val horaSaidaExtra = intent.getStringExtra("horaSaida").orEmpty()
        val observacaoExtra = intent.getStringExtra("observacao").orEmpty()

        if (cpfExtra.isNotBlank()) binding.editTextCPF.setText(cpfExtra)
        if (placaExtra.isNotBlank()) binding.editTextPlaca.setText(placaExtra)
        if (nomeExtra.isNotBlank()) binding.editTextNome.setText(nomeExtra)
        if (modeloExtra.isNotBlank()) binding.editTextModeloCor.setText(modeloExtra)
        if (motivoExtra.isNotBlank()) binding.editTextMotivo.setText(motivoExtra)
        if (dataExtra.isNotBlank()) binding.editTextData.setText(dataExtra)
        if (horaEntradaExtra.isNotBlank()) binding.editTextHoraEntrada.setText(horaEntradaExtra)
        if (horaSaidaExtra.isNotBlank()) binding.editTextHoraSaida.setText(horaSaidaExtra)
        if (observacaoExtra.isNotBlank()) binding.editTextObservacao.setText(observacaoExtra)
    }

    // Dropdown Motivo
    private fun setupMotivoDropdown() {
        val motivos = listOf("Funcionário", "Visita", "Usuário")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, motivos)
        binding.editTextMotivo.setAdapter(adapter)
    }

    // Inserir " / " automaticamente após o primeiro espaço
    private fun setupModeloCorMask() {
        binding.editTextModeloCor.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                val text = s?.toString() ?: return
                if (text.contains(" ") && !text.contains("/")) {
                    isUpdating = true
                    val newText = text.replaceFirst(" ", " / ")
                    binding.editTextModeloCor.setText(newText)
                    binding.editTextModeloCor.setSelection(newText.length)
                    isUpdating = false
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Máscaras para CPF, Data, Hora e Placa
    private fun setupMasks() {
        // CPF
        binding.editTextCPF.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "###.###.###-##"
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
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
                binding.editTextCPF.setText(formatted)
                binding.editTextCPF.setSelection(formatted.length)
                isUpdating = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Data
        binding.editTextData.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                val digits = (s ?: "").toString().replace("/", "")
                val sb = StringBuilder()
                for (i in digits.indices) {
                    sb.append(digits[i])
                    if ((i == 1 || i == 3) && digits.length > i + 1) sb.append("/")
                }
                isUpdating = true
                binding.editTextData.setText(sb.toString())
                binding.editTextData.setSelection(sb.length)
                isUpdating = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Hora
        fun attachHourMask(edit: EditText) {
            edit.addTextChangedListener(object : TextWatcher {
                private var isUpdating = false
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (isUpdating) return
                    val digits = (s ?: "").toString().replace(":", "")
                    val sb = StringBuilder()
                    for (i in digits.indices) {
                        sb.append(digits[i])
                        if (i == 1 && digits.length > 2) sb.append(":")
                    }
                    isUpdating = true
                    edit.setText(sb.toString())
                    edit.setSelection(sb.length)
                    isUpdating = false
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        attachHourMask(binding.editTextHoraEntrada)
        attachHourMask(binding.editTextHoraSaida)

        // Placa
        binding.editTextPlaca.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                val raw = (s ?: "").toString().replace("-", "").uppercase()
                val limited = raw.take(7)
                val sb = StringBuilder()
                for (i in limited.indices) {
                    sb.append(limited[i])
                    if (i == 2 && limited.length > 3) sb.append("-")
                }
                isUpdating = true
                binding.editTextPlaca.setText(sb.toString())
                binding.editTextPlaca.setSelection(sb.length)
                isUpdating = false
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupListeners() {
        binding.imageViewBack.setOnClickListener { finish() }
        binding.btnVoltar.setOnClickListener { finish() }
        binding.btnSalvar.setOnClickListener { handleSave() }
        binding.editTextData.setOnClickListener { showDatePickerDialog() }
        binding.editTextHoraEntrada.setOnClickListener { showTimePickerDialog(binding.editTextHoraEntrada) }
        binding.editTextHoraSaida.setOnClickListener { showTimePickerDialog(binding.editTextHoraSaida) }
    }

    private fun handleSave() {
        if (binding.editTextNome.text.isNullOrEmpty() || binding.editTextPlaca.text.isNullOrEmpty()) {
            Toast.makeText(this, "Nome e Placa são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = VehicleEntity(
            id = vehicleId ?: 0,
            motivo = binding.editTextMotivo.text.toString(),
            nomeCompleto = binding.editTextNome.text.toString(),
            cpf = binding.editTextCPF.text.toString(),
            placaCarro = binding.editTextPlaca.text.toString(),
            modeloCor = binding.editTextModeloCor.text.toString(),
            data = binding.editTextData.text.toString(),
            horarioEntrada = binding.editTextHoraEntrada.text.toString(),
            horarioSaida = binding.editTextHoraSaida.text.toString(),
            observacao = binding.editTextObservacao.text.toString()
        )

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            if (vehicleId != null) {
                db.vehicleDao().update(entry)
            } else {
                db.vehicleDao().insert(entry)
            }
            runOnUiThread {
                Toast.makeText(this@AddEntryActivity, "Veículo registrado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePickerDialog(editText: EditText) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
            editText.setText(formattedTime)
        }
        TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateDateInView() {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.editTextData.setText(sdf.format(calendar.time))
    }
}
