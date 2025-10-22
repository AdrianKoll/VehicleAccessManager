package com.aplicativo_text_10.data

import androidx.room.*
import com.aplicativo_text_10.model.VehicleEntity

@Dao
interface VehicleDao {
    @Insert
    suspend fun insert(entry: VehicleEntity)

    @Query("SELECT * FROM vehicle_entries ORDER BY id DESC")
    suspend fun getAll(): List<VehicleEntity>

    @Update
    suspend fun update(entry: VehicleEntity)

    @Delete
    suspend fun delete(entry: VehicleEntity)

    @Query("SELECT * FROM vehicle_entries WHERE placaCarro LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchByPlaca(query: String): List<VehicleEntity>

    @Query("SELECT * FROM vehicle_entries WHERE cpf LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchByCpf(query: String): List<VehicleEntity>

    @Query("SELECT * FROM vehicle_entries WHERE nomeCompleto LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchByNome(query: String): List<VehicleEntity>

    @Query("SELECT * FROM vehicle_entries WHERE motivo LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchByMotivo(query: String): List<VehicleEntity>

    @Query("""
        SELECT * FROM vehicle_entries 
        WHERE placaCarro LIKE '%' || :query || '%' 
           OR nomeCompleto LIKE '%' || :query || '%'
           OR cpf LIKE '%' || :query || '%'
           OR motivo LIKE '%' || :query || '%'
           OR modeloCor LIKE '%' || :query || '%'
           OR data LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    suspend fun searchAll(query: String): List<VehicleEntity>

    @Query("""
        SELECT * FROM vehicle_entries  
        WHERE UPPER(cpf) = UPPER(:licenseCpf)
        AND UPPER(placaCarro) = UPPER(:licensePlate)
        LIMIT 1
    """)
    suspend fun checkVehicleByNameAndPlate(licenseCpf: String, licensePlate: String): VehicleEntity?

    // 🔹 Método para autocomplete de CPF
    @Query("SELECT DISTINCT cpf FROM vehicle_entries WHERE cpf LIKE :prefix || '%' ORDER BY cpf ASC")
    suspend fun getCpfsStartingWith(prefix: String): List<String>
}
