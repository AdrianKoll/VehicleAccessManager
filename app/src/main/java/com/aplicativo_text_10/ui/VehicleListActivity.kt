package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.aplicativo_text_10.R
import com.aplicativo_text_10.data.AppDatabase
import com.aplicativo_text_10.model.VehicleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VehicleListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VehicleAdapter
    private lateinit var editTextSearch: EditText
    private lateinit var textViewNoResults: TextView
    private lateinit var chipAll: Chip
    private lateinit var chipPlaca: Chip
    private lateinit var chipNome: Chip
    private lateinit var chipCpf: Chip
    private lateinit var chipMotivo: Chip
    private lateinit var chipData: Chip
    private lateinit var database: AppDatabase

    private var allVehicleEntities = emptyList<VehicleEntity>()
    private var currentSearchType = "all"
    private val modo: String = "editar"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_list)

        database = AppDatabase.getDatabase(applicationContext)

        initViews()
        setupRecyclerView()
        setupSearch()
        setupChips()
    }

    override fun onResume() {
        super.onResume()
        loadVehicles()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewVehicles)
        editTextSearch = findViewById(R.id.editTextSearch)
        textViewNoResults = findViewById(R.id.textViewNoResults)
        chipAll = findViewById(R.id.chipAll)
        chipPlaca = findViewById(R.id.chipPlaca)
        chipNome = findViewById(R.id.chipNome)
        chipCpf = findViewById(R.id.chipCpf)
        chipMotivo = findViewById(R.id.chipMotivo)
        chipData = findViewById(R.id.chipData)

        findViewById<View>(R.id.imageViewBack)?.setOnClickListener { finish() }

        chipAll.isChecked = true
        currentSearchType = "all"
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = VehicleAdapter(
            items = emptyList(),
            onCheck = { showVehicleDetails(it) },
            onEdit = { editVehicle(it) },
            onDelete = { deleteVehicle(it) },
            modo = modo
        )
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                performSearch(s?.toString()?.trim().orEmpty())
            }
        })
    }

    private fun setupChips() {
        val chips = listOf(chipAll, chipPlaca, chipNome, chipCpf, chipMotivo, chipData)
        chips.forEach { chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chips.filter { it != chip }.forEach { it.isChecked = false }
                    currentSearchType = when (chip.id) {
                        R.id.chipPlaca -> "placa"
                        R.id.chipNome -> "nome"
                        R.id.chipCpf -> "cpf"
                        R.id.chipMotivo -> "motivo"
                        R.id.chipData -> "data"
                        else -> "all"
                    }
                    performSearch(editTextSearch.text.toString().trim())
                }
            }
        }
    }

    private fun loadVehicles() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val lista = database.vehicleDao().getAll()
                allVehicleEntities = lista
                withContext(Dispatchers.Main) { updateAdapter(lista) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateAdapter(emptyList())
                    Toast.makeText(
                        this@VehicleListActivity,
                        "Erro ao carregar veículos: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun deleteVehicle(vehicleEntity: VehicleEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                database.vehicleDao().delete(vehicleEntity)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VehicleListActivity, "Veículo excluído!", Toast.LENGTH_SHORT).show()
                    loadVehicles()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@VehicleListActivity,
                        "Erro ao excluir: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun performSearch(query: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val results = if (query.isEmpty()) {
                allVehicleEntities
            } else {
                when (currentSearchType) {
                    "placa" -> database.vehicleDao().searchByPlaca(query)
                    "nome" -> database.vehicleDao().searchByNome(query)
                    "cpf" -> database.vehicleDao().searchByCpf(query)
                    "motivo" -> database.vehicleDao().searchByMotivo(query)
                    "data" -> allVehicleEntities.filter { it.data.contains(query, true) }.sortedBy { it.horarioEntrada }
                    else -> database.vehicleDao().searchAll(query)
                }
            }
            withContext(Dispatchers.Main) { updateAdapter(results) }
        }
    }

    private fun updateAdapter(vehicleEntities: List<VehicleEntity>) {
        adapter.updateItems(vehicleEntities)
        textViewNoResults.visibility = if (vehicleEntities.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (vehicleEntities.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showVehicleDetails(vehicleEntity: VehicleEntity) {
        Toast.makeText(this, "Detalhes de ${vehicleEntity.nomeCompleto}", Toast.LENGTH_SHORT).show()
    }

    private fun editVehicle(vehicleEntity: VehicleEntity) {
        val intent = Intent(this, AddEntryActivity::class.java).apply {
            putExtra("vehicleId", vehicleEntity.id)
            putExtra("nome", vehicleEntity.nomeCompleto)
            putExtra("cpf", vehicleEntity.cpf)
            putExtra("motivo", vehicleEntity.motivo)
            putExtra("placa", vehicleEntity.placaCarro)
            putExtra("modeloCor", vehicleEntity.modeloCor)
            putExtra("data", vehicleEntity.data)
            putExtra("horaEntrada", vehicleEntity.horarioEntrada)
            putExtra("horaSaida", vehicleEntity.horarioSaida)
            putExtra("observacao", vehicleEntity.observacao)
            putExtra("modo", "editar")
        }
        startActivity(intent)
    }
}
