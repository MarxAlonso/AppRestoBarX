package com.example.apprestobarx.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PromocionesEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promocionesDao(): PromocionesDao
}