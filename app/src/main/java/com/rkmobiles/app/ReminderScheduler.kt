package com.rkmobiles.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderScheduler {
    fun schedule(context: Context){
        val alarm=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent=Intent(context, ReminderReceiver::class.java)
        val pi=PendingIntent.getBroadcast(context,1001,intent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val now=Calendar.getInstance(); val next=Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY,10); set(Calendar.MINUTE,0); set(Calendar.SECOND,0); set(Calendar.MILLISECOND,0); if(!after(now)) add(Calendar.DAY_OF_YEAR,1) }
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,next.timeInMillis,AlarmManager.INTERVAL_DAY,pi)
    }
}
