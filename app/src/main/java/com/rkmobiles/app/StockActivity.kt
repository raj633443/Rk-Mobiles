package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StockActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_stock);render()}
 private fun render(){val box=findViewById<LinearLayout>(R.id.stockList);box.removeAllViews();val db=DbHelper(this)
 package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StockActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_stock);render()}
 private fun render(){val box=findViewById<LinearLayout>(R.id.stockList);box.removeAllViews();val db=DbHelper(this)
  db.stockRows().forEach{r->val t=TextView(this);t.text="${r[1]} ${r[2]}\nQty: ${r[3]}  •  Buy ₹${r[4]}  •  Sell ₹${r[5]}\nIMEI: ${r[6]}";t.textSize=14f;t.setPadding(16,16,16,16);t.setBackgroundResource(R.drawable.card_bg);t.setTextColor(0xFF111827.toInt());box.addView(t)
  }
 }
}
}
