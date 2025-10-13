package com.example.apprestobarx.data.repository

import com.example.apprestobarx.data.dao.BebidasDao
import com.example.apprestobarx.data.local.BebidaEntity
import com.example.apprestobarx.network.RetrofitClient
import android.content.Context
import android.util.Log
import com.example.apprestobarx.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class BebidasRepository(private val context: Context, private val db: AppDatabase) {
    private val dao = db.bebidasDao()

    suspend fun getBebidas(): List<BebidaEntity> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getBebidas().execute()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data

                val entities = data.map {
                    BebidaEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        type = it.type
                    )
                }

                dao.clearAll()
                dao.insertAll(entities)
                Log.i("Repository", "Bebidas sincronizadas con API ✅")
                entities
            } else {
                Log.w("Repository", "API falló, usando datos locales")
                dao.getAll()
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error: ${e.message}")
            dao.getAll()
        }
    }
}
