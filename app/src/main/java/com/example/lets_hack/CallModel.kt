package com.example.lets_hack

data class CallModel(
    val number: String = "",
    val name: String = "Unknown",
    val type: Int = 0,
    val timestamp: Long = 0L,
    val duration: Int = 0
)