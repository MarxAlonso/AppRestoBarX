package com.example.apprestobarx.data

import androidx.room.*
import com.example.apprestobarx.data.local.PromocionesEntity

@Dao
interface PromocionesDao {

    @Query("SELECT * FROM promociones")
    suspend fun getAll(): List<PromocionesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(promociones: List<PromocionesEntity>)

    @Query("DELETE FROM promociones")
    suspend fun deleteAll()
}