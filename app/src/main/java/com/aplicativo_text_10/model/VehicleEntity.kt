package com.aplicativo_text_10.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_entries")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val motivo: String,
    val nomeCompleto: String,
    val cpf: String,
    val placaCarro: String,
    val modeloCor: String,
    val data: String,
    val horarioEntrada: String,
    val horarioSaida: String? = null,
    val observacao: String? = null
)