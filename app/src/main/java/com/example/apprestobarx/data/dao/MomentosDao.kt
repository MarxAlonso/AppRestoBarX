package com.example.apprestobarx.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.apprestobarx.data.local.MomentoEntity

@Dao
interface MomentosDao {

    @Insert
    suspend fun insert(moment: MomentoEntity)

    @Update
    suspend fun update(moment: MomentoEntity)

    @Delete
    suspend fun delete(moment: MomentoEntity)

    @Query("SELECT * FROM momentos ORDER BY fecha DESC")
    suspend fun getAll(): List<MomentoEntity>

}
