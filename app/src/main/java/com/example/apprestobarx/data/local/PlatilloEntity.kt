package com.example.apprestobarx.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "platillos")
data class PlatilloEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String
)