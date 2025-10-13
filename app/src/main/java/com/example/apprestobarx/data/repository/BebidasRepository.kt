package com.example.apprestobarx.data.repository

import com.example.apprestobarx.data.dao.BebidasDao
import com.example.apprestobarx.data.local.BebidaEntity
import com.example.apprestobarx.network.RetrofitClient
import android.content.Context
import android.util.Log
import com.example.apprestobarx.data.AppDatabase

class BebidasRepository(private val context: Context, private val db: AppDatabase) {

    suspend fun getBebidas(): List<BebidaEntity> {
        return try {
            val response = RetrofitClient.instance.getBebidas().execute()
            if (response.isSuccessful && response.body() != null) {
                val bebidas = response.body()!!.data.map {
                    BebidaEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        type = it.type
                    )
                }

                // Guardamos en la base local
                db.bebidasDao().clearAll()
                db.bebidasDao().insertAll(bebidas)

                bebidas
            } else {
                Log.e("BebidasRepository", "Error: ${response.errorBody()?.string()}")
                db.bebidasDao().getAll()
            }
        } catch (e: Exception) {
            Log.e("BebidasRepository", "Fallo en conexión: ${e.message}")
            db.bebidasDao().getAll() // Si no hay red, usamos datos locales
        }
    }
}