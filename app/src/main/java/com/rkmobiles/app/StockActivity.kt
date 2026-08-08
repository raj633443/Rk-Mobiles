package com.rkmobiles.app
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
class StockActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_stock);load()
  findViewById<Button>(R.id.btnAddStock).setOnClickListener{
   val i=findViewById<EditText>(R.id.etItem).text.toString(); val q=findViewById<EditText>(R.id.etQty).text.toString().toIntOrNull()?:0
   val buy=findViewById<EditText>(R.id.etBuy).text.toString().toDoubleOrNull()?:0.0; val sell=findViewById<EditText>(R.id.etSell).text.toString().toDoubleOrNull()?:0.0
   if(i.isBlank()||q<=0){Toast.makeText(this,"Enter item and quantity",Toast.LENGTH_SHORT).show();return@setOnClickListener}
   DbHelper(this).writableDatabase.execSQL("INSERT INTO stock(item,qty,buy,sell) VALUES(?,?,?,?)",arrayOf(i,q,buy,sell));load()
  }
 }
 private fun load(){val a=ArrayList<String>();DbHelper(this).readableDatabase.rawQuery("SELECT item,qty,buy,sell FROM stock ORDER BY item",null).use{c->while(c.moveToNext())a.add("${c.getString(0)}  •  ${c.getInt(1)} pcs  • Buy ₹${c.getDouble(2).toInt()} • Sell ₹${c.getDouble(3).toInt()}")};findViewById<ListView>(R.id.listStock).adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,a)}
}
