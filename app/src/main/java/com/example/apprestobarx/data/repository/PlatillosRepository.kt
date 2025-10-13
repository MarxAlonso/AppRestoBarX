package com.example.apprestobarx.data.repository

import android.util.Log
import com.example.apprestobarx.data.AppDatabase
import com.example.apprestobarx.data.local.PlatilloEntity
import com.example.apprestobarx.models.Platillo
import com.example.apprestobarx.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlatillosRepository(private val db: AppDatabase) {

    private val platilloDao = db.platilloDao()

    suspend fun getPlatillos(): List<PlatilloEntity> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getPlatillos().execute()

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data

                val entities = data.map {
                    PlatilloEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl
                    )
                }

                // Sincroniza con SQLite
                platilloDao.deleteAll()
                platilloDao.insertAll(entities)
                Log.i("Repository", "Platillos actualizados desde API ✅")
                entities
            } else {
                Log.w("Repository", "Fallo en API, usando datos locales")
                platilloDao.getAll()
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error: ${e.message}")
            platilloDao.getAll() // sin internet → carga local
        }
    }
}