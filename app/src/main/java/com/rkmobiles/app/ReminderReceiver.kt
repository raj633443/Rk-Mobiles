package com.rkmobiles.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "RK Mobile: check pending payments", Toast.LENGTH_SHORT).show()
    }
}
