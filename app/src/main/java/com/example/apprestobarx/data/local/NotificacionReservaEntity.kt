package com.example.apprestobarx.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificaciones_reserva")
data class NotificacionReservaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreCliente: String,
    val fecha: String,
    val hora: String,
    val mensaje: String
)
