package com.example.apprestobarx.network

import com.example.apprestobarx.models.Platillo

data class DishesResponse(
    val success: Boolean,
    val data: List<Platillo>
)
