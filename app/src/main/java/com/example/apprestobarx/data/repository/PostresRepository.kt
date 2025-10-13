package com.example.apprestobarx.data.repository

import android.content.Context
import android.util.Log
import com.example.apprestobarx.data.AppDatabase
import com.example.apprestobarx.data.local.PostreEntity
import com.example.apprestobarx.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/**
 * Repositorio encargado de manejar la sincronización de datos de Postres
 * entre la API (Remota) y la base de datos local (Room).
 */
class PostresRepository(private val context: Context, private val db: AppDatabase) {

    private val dao = db.postresDao()

    /**
     * Obtiene la lista de postres.
     * Si hay conexión y la API responde correctamente, actualiza los datos locales.
     * Si falla la API, usa los datos guardados en SQLite.
     */
    suspend fun getPostres(): List<PostreEntity> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getPostres().execute()

            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data

                // Convertimos los datos de la API a entidades de Room
                val entities = data.map {
                    PostreEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        calories = it.calories
                    )
                }

                // Sincronizar con base local
                dao.clearAll()
                dao.insertAll(entities)
                Log.i("Repository", "Postres sincronizados con API ✅")

                entities
            } else {
                Log.w("Repository", "Error en API (${response.code()}), usando datos locales")
                dao.getAll()
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error al obtener postres: ${e.message}")
            dao.getAll()
        }
    }
}