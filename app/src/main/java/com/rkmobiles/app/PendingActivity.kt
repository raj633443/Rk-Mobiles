package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PendingActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_pending);render()}
 private fun render(){val db=DbHelper(this);val box=findViewById<LinearLayout>(R.id.pendingList);box.removeAllViews()
 row.setBackgroundResource(R.drawable/card)  // Missing closing bracket
   val t=TextView(this);t.text="${r[1]}\nDue ₹${r[3]}";t.textSize=15f;row.addView(t,LinearLayout.LayoutParams(0,-2,1f))
   val btn=Button(this);btn.text="PAID";btn.setOnClickListener{db.markPendingPaid(r[0].toLong());render()};row.addView(btn)
   box.addView(row,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,10)})
  }
 }
}
