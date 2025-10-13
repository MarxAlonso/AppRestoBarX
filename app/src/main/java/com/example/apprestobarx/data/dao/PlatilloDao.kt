package com.example.apprestobarx.data.dao

import androidx.room.*
import com.example.apprestobarx.data.local.PlatilloEntity

@Dao
interface PlatilloDao {

    @Query("SELECT * FROM platillos")
    suspend fun getAll(): List<PlatilloEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(platillos: List<PlatilloEntity>)

    @Query("DELETE FROM platillos")
    suspend fun deleteAll()
}