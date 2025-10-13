package com.example.apprestobarx.data.repository

import android.content.Context
import android.util.Log
import com.example.apprestobarx.data.AppDatabase
import com.example.apprestobarx.data.local.PromocionesEntity
import com.example.apprestobarx.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PromocionesRepository(private val context: Context, private val db: AppDatabase) {

    private val promocionesDao = db.promocionesDao()

    suspend fun getPromociones(): List<PromocionesEntity> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = RetrofitClient.instance.getPromociones().execute()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data

                // Convertir a entidades para Room
                val entities = data.map {
                    PromocionesEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl
                    )
                }

                // Actualizar base local
                promocionesDao.deleteAll()
                promocionesDao.insertAll(entities)

                Log.i("Repository", "Datos sincronizados desde API ✅")
                entities
            } else {
                Log.w("Repository", "Fallo API, cargando datos locales...")
                promocionesDao.getAll()
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error al conectar con API: ${e.message}")
            promocionesDao.getAll() // Si no hay internet → datos locales
        }
    }
}