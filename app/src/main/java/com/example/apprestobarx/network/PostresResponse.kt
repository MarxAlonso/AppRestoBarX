package com.example.apprestobarx.network

import com.example.apprestobarx.models.Postres

data class PostresResponse(
    val success: Boolean,
    val data: List<Postres>
)