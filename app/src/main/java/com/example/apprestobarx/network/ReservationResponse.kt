package com.example.apprestobarx.network

import com.example.apprestobarx.models.Reservation

data class ReservationResponse(
    val success: Boolean,
    val data: Reservation?,
    val message: String
)