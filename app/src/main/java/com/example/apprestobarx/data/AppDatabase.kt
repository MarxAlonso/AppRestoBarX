package com.example.apprestobarx.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.apprestobarx.data.dao.BebidasDao
import com.example.apprestobarx.data.dao.MomentosDao
import com.example.apprestobarx.data.dao.NotificacionReservaDao
import com.example.apprestobarx.data.dao.PlatilloDao
import com.example.apprestobarx.data.dao.PostresDao
import com.example.apprestobarx.data.dao.UsuarioDao
import com.example.apprestobarx.data.local.BebidaEntity
import com.example.apprestobarx.data.local.MomentoEntity
import com.example.apprestobarx.data.local.NotificacionReservaEntity
import com.example.apprestobarx.data.local.PlatilloEntity
import com.example.apprestobarx.data.local.PostreEntity
import com.example.apprestobarx.data.local.PromocionesEntity
import com.example.apprestobarx.data.local.UsuarioEntity

@Database(
    entities = [
        PromocionesEntity::class,
        PlatilloEntity::class,
        BebidaEntity::class,
        PostreEntity::class,
        UsuarioEntity::class,
        NotificacionReservaEntity::class,
        MomentoEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun promocionesDao(): PromocionesDao
    abstract fun platilloDao(): PlatilloDao
    abstract fun bebidasDao(): BebidasDao
    abstract fun postresDao(): PostresDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun notificacionReservaDao(): NotificacionReservaDao
    abstract fun momentosDao(): MomentosDao
}