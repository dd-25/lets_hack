package com.example.lets_hack

import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {
    private val databaseRef = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DB_URL).getReference("notifications")

    fun uploadNotification(notification: NotificationModel) {
        val key = databaseRef.push().key ?: return
        databaseRef.child(key).setValue(notification)
    }
}