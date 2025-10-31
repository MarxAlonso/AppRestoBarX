package com.example.apprestobarx.models
data class Reservation(
    val reservationType: String,
    val fullName: String,
    val numPeople: Int,
    val reservationDate: String,
    val reservationTime: String,
    val eventDetails: String? = null
)