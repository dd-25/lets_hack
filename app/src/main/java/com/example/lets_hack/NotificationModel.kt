package com.example.lets_hack

data class NotificationModel(
    val appName: String = "",
    val title: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)