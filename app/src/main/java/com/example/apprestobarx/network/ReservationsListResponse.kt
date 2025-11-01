package com.example.apprestobarx.network

import com.example.apprestobarx.models.Reservation

data class ReservationsListResponse(
    val success: Boolean,
    val data: List<Reservation>,
    val message: String
)