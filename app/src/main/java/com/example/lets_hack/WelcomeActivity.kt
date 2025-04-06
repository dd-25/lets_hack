package com.example.lets_hack

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.lets_hack.databinding.ActivityWelcomeBinding
import android.util.Log

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val requestCodePermissions = 101
    private val TAG = "WelcomeActivity"

    private val requiredPermissions = mutableListOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CONTACTS,
    ).apply {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // Android 12 and below
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // Android 13+
            add(Manifest.permission.READ_MEDIA_IMAGES)
            add(Manifest.permission.READ_MEDIA_VIDEO)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueButton.setOnClickListener {
            if (!hasAllPermissions(this)) {
                ActivityCompat.requestPermissions(this, requiredPermissions, requestCodePermissions)
            } else if (!isNotificationServiceEnabled(this)) {
                showNotificationPermissionDialog()
            } else {
                goToMain()
            }
        }
    }

    private fun hasAllPermissions(context: Context): Boolean {
        val missingPermissions = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            Log.d("PermissionCheck", "Missing permissions: $missingPermissions")
        }

        return missingPermissions.isEmpty()
    }

    private fun isNotificationServiceEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        return enabledListeners?.contains(context.packageName) == true
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Access Required")
            .setMessage("Please allow notification access for this app to work properly.")
            .setPositiveButton("Go to Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == requestCodePermissions) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                if (!isNotificationServiceEnabled(this)) {
                    showNotificationPermissionDialog()
                } else {
                    goToMain()
                }
            } else {
                Toast.makeText(this, "Please grant all permissions to continue", Toast.LENGTH_SHORT).show()
            }
        }
    }
}