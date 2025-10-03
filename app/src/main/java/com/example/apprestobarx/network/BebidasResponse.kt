package com.example.apprestobarx.network

import com.example.apprestobarx.models.Bebidas

data class BebidasResponse(
    val success: Boolean,
    val data: List<Bebidas>
)