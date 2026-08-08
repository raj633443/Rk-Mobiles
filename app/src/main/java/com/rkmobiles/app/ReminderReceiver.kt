package com.rkmobiles.app
import android.content.*
import android.net.Uri
class ReminderReceiver:BroadcastReceiver(){
 override fun onReceive(context:Context,intent:Intent?){
  val c=DbHelper(context).readableDatabase.rawQuery("SELECT customer,phone,pending FROM repairs WHERE pending>0 ORDER BY date LIMIT 1",null)
  c.use{if(it.moveToFirst()){val phone=(it.getString(1)?:"").filter(Char::isDigit);if(phone.length>=10){val msg="Hello ${it.getString(0)},\nYour pending amount is ₹${it.getDouble(2).toInt()}.\nPlease visit RK Mobiles and clear the payment.\nThank you.";val i=Intent(Intent.ACTION_VIEW,Uri.parse("https://wa.me/91$phone?text="+Uri.encode(msg)));i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);context.startActivity(i)}}}
 }
}
