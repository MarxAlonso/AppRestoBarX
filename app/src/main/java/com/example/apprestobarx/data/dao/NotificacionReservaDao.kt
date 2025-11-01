package com.example.apprestobarx.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.apprestobarx.data.local.NotificacionReservaEntity

@Dao
interface NotificacionReservaDao {
    @Insert
    suspend fun insert(notificacion: NotificacionReservaEntity)

    @Query("SELECT * FROM notificaciones_reserva")
    suspend fun getAll(): List<NotificacionReservaEntity>
}