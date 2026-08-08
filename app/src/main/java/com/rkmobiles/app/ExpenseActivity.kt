package com.rkmobiles.app
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
class ExpenseActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_expense);load()
  findViewById<Button>(R.id.btnAddExpense).setOnClickListener{
   val t=findViewById<EditText>(R.id.etTitle).text.toString();val a=findViewById<EditText>(R.id.etAmount).text.toString().toDoubleOrNull()?:0.0
   if(t.isBlank()||a<=0)return@setOnClickListener
   DbHelper(this).writableDatabase.execSQL("INSERT INTO expenses(title,amount,date) VALUES(?,?,date('now'))",arrayOf(t,a));load()
  }
 }
 private fun load(){val a=ArrayList<String>();DbHelper(this).readableDatabase.rawQuery("SELECT title,amount,date FROM expenses ORDER BY id DESC",null).use{c->while(c.moveToNext())a.add("${c.getString(0)}  ₹${c.getDouble(1).toInt()}  ${c.getString(2)}")};findViewById<ListView>(R.id.listExpense).adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,a)}
}
