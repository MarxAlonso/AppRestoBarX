package com.example.apprestobarx.network

import com.example.apprestobarx.models.Postres
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("dishes")
    fun getPlatillos(): Call<DishesResponse>

    @GET("drinks")
    fun getBebidas(): Call<BebidasResponse>

    @GET("desserts")
    fun getPostres(): Call<List<Postres>>
}
