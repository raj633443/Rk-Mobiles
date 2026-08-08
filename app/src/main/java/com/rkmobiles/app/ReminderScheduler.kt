package com.rkmobiles.app
import android.app.*
import android.content.Context
import java.util.Calendar
object ReminderScheduler{
 fun schedule(context:Context){
  val alarm=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  val pi=PendingIntent.getBroadcast(context,77,android.content.Intent(context,ReminderReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
  val now=Calendar.getInstance(); val at=Calendar.getInstance().apply{set(Calendar.HOUR_OF_DAY,10);set(Calendar.MINUTE,0);set(Calendar.SECOND,0);set(Calendar.MILLISECOND,0);if(!after(now))add(Calendar.DATE,1)}
  alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,at.timeInMillis,AlarmManager.INTERVAL_DAY,pi)
 }
}
