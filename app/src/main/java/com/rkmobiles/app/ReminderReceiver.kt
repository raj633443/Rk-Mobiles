package com.rkmobiles.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class ReminderReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent?){
        val db=DbHelper(context); val amount=db.pendingTotal(); if(amount<=0) return
        val nm=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel="pending"
        if(Build.VERSION.SDK_INT>=26) nm.createNotificationChannel(NotificationChannel(channel,"Pending reminders",NotificationManager.IMPORTANCE_DEFAULT))
        val n=NotificationCompat.Builder(context,channel).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("RK Mobiles – Pending Reminder").setContentText("Pending amount: Rs ${String.format("%.2f",amount)}").setAutoCancel(true).build()
        nm.notify(1001,n)
    }
}
