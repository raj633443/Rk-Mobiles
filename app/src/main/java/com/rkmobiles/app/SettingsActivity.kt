package com.rkmobiles.app
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
class SettingsActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_settings)
  findViewById<Button>(R.id.btnSavePin).setOnClickListener{val p=findViewById<EditText>(R.id.etNewPin).text.toString();if(p.length<4)Toast.makeText(this,"PIN must be 4-6 digits",Toast.LENGTH_SHORT).show()else{SessionManager(this).savePin(p);Toast.makeText(this,"PIN changed",Toast.LENGTH_SHORT).show()}}
  findViewById<Button>(R.id.btnSetReminder).setOnClickListener{ReminderScheduler.schedule(this);Toast.makeText(this,"10:00 AM reminder set",Toast.LENGTH_SHORT).show()}
 }
}
