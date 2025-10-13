package com.example.apprestobarx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apprestobarx.data.dao.PlatilloDao
import com.example.apprestobarx.data.local.PlatilloEntity
import com.example.apprestobarx.data.local.PromocionesEntity

@Database(
    entities = [PromocionesEntity::class, PlatilloEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promocionesDao(): PromocionesDao
    abstract fun platilloDao(): PlatilloDao
}