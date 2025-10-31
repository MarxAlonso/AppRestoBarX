package com.example.apprestobarx.network

import com.example.apprestobarx.models.Postres
import com.example.apprestobarx.models.Reservation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @GET("dishes")
    fun getPlatillos(): Call<DishesResponse>

    @GET("drinks")
    fun getBebidas(): Call<BebidasResponse>

    @GET("desserts")
    fun getPostres(): Call<PostresResponse>

    @GET("promotions")
    fun getPromociones(): Call<PromocionesResponse>

    @POST("reservations")
    fun createReservation(@Body reservation: Reservation): Call<ReservationResponse>
}
