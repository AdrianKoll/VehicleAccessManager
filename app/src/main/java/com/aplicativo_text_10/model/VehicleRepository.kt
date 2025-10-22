package com.aplicativo_text_10.model

import java.text.SimpleDateFormat
import java.util.Locale

object VehicleRepository {
    private val vehicleList = mutableListOf<VehicleEntity>()
    private var nextId = 1

    init {
        // Dados de exemplo para teste
        add(
            VehicleEntity(
                motivo = "Manutenção",
                nomeCompleto = "Lucas",
                cpf = "AAA-0000",
                placaCarro = "ABC-1234",
                modeloCor = "Onix Prata",
                data = "15/10/2025",
                horarioEntrada = "09:30",
                horarioSaida = "09:50"
            )
        )
        add(
            VehicleEntity(
                motivo = "Visita",
                nomeCompleto = "Adrian",
                cpf = "AAA-0000",
                placaCarro = "ABC-1234",
                modeloCor = "Onix Prata",
                data = "15/10/2025",
                horarioEntrada = "09:30"
            )
        )
        add(
            VehicleEntity(
                motivo = "Manutenção",
                nomeCompleto = "João Silva",
                cpf = "AAA-0000",
                placaCarro = "ABC-1234",
                modeloCor = "Onix Prata",
                data = "15/10/2025",
                horarioEntrada = "09:30",
                horarioSaida = "09:50"
            )
        )
    }

    fun getAllSortedByDate(): List<VehicleEntity> {
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return vehicleList.sortedWith(compareByDescending { record ->
            try {
                dateTimeFormat.parse("${record.data} ${record.horarioEntrada}")
            } catch (e: Exception) {
                null
            }
        })
    }

    fun add(vehicle: VehicleEntity) {
        vehicleList.add(vehicle.copy(id = nextId++))
    }

    fun update(updatedVehicle: VehicleEntity): Boolean {
        val index = vehicleList.indexOfFirst { it.id == updatedVehicle.id }
        return if (index != -1) {
            vehicleList[index] = updatedVehicle
            true
        } else {
            false
        }
    }

    fun delete(id: Int): Boolean {
        return vehicleList.removeIf { it.id == id }
    }

    fun findById(id: Int): VehicleEntity? {
        return vehicleList.find { it.id == id }
    }
}