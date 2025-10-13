package com.example.apprestobarx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apprestobarx.data.dao.BebidasDao
import com.example.apprestobarx.data.dao.PlatilloDao
import com.example.apprestobarx.data.dao.PostresDao
import com.example.apprestobarx.data.local.BebidaEntity
import com.example.apprestobarx.data.local.PlatilloEntity
import com.example.apprestobarx.data.local.PostreEntity
import com.example.apprestobarx.data.local.PromocionesEntity

@Database(
    entities = [
        PromocionesEntity::class,
        PlatilloEntity::class,
        BebidaEntity::class,
        PostreEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promocionesDao(): PromocionesDao
    abstract fun platilloDao(): PlatilloDao
    abstract fun bebidasDao(): BebidasDao
    abstract fun postresDao(): PostresDao
}
