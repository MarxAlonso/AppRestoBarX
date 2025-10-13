package com.example.apprestobarx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apprestobarx.data.local.PostreEntity

@Dao
interface PostresDao {
    @Query("SELECT * FROM postres")
    suspend fun getAll(): List<PostreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(postres: List<PostreEntity>)

    @Query("DELETE FROM postres")
    suspend fun clearAll()
}
