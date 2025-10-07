package com.example.apprestobarx.models

data class Message(
    val text: String,
    val isUser: Boolean, // true = User, false = Bot
    val options: List<String>? = null // Para sugerencias del bot
)