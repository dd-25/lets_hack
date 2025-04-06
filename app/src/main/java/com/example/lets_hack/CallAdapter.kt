package com.example.lets_hack

import android.provider.CallLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallAdapter(private val callLogs: List<CallModel>) :
    RecyclerView.Adapter<CallAdapter.CallViewHolder>() {

    class CallViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.callName)
        val number: TextView = view.findViewById(R.id.callNumber)
        val type: TextView = view.findViewById(R.id.callType)
        val timestamp: TextView = view.findViewById(R.id.callTimestamp)
        val duration: TextView = view.findViewById(R.id.callDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_call, parent, false)
        return CallViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val call = callLogs[position]
        holder.name.text = call.name
        holder.number.text = call.number
        holder.type.text = when (call.type) {
            CallLog.Calls.INCOMING_TYPE -> "Incoming"
            CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
            CallLog.Calls.MISSED_TYPE -> "Missed"
            else -> "Unknown"
        }
        holder.timestamp.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
            Date(call.timestamp)
        )
        holder.duration.text = "${call.duration} sec"
    }

    override fun getItemCount(): Int = callLogs.size
}