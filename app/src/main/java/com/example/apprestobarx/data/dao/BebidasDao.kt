package com.example.apprestobarx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apprestobarx.data.local.BebidaEntity

@Dao
interface BebidasDao {

    // Insertar lista de bebidas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bebidas: List<BebidaEntity>)

    // Obtener todas las bebidas almacenadas
    @Query("SELECT * FROM bebidas")
    suspend fun getAll(): List<BebidaEntity>

    // Eliminar todas las bebidas
    @Query("DELETE FROM bebidas")
    suspend fun clearAll()
}