package com.example.apprestobarx.network

import com.example.apprestobarx.models.Promociones

data class PromocionesResponse(
    val success: Boolean,
    val data: List<Promociones>
)