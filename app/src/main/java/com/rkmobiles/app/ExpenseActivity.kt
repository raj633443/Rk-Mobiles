package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ExpenseActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_expense);val db=DbHelper(this);render(db)
  findViewById<Button>(R.id.btnSaveExpense).setOnClickListener{
   val d=findViewById<EditText>(R.id.etExpenseDesc).text.toString().trim();val a=findViewById<EditText>(R.id.etExpenseAmount).text.toString().toDoubleOrNull()
   if(d.isBlank()||a==null||a<0){toast("Enter valid expense");return@setOnClickListener};db.insertExpense(d,a);toast("Expense saved");render(db)
  }
 }
 private fun render(db:DbHelper){val box=findViewById<LinearLayout>(R.id.expenseList);box.removeAllViews();db.expenseRows().forEach{r->val t=TextView(this);t.text="${r[1]}\n₹ ${r[2]}";t.textSize=14f;t.setPadding(16,16,16,16);t.setBackgroundResource(R.drawable/card);box.addView(t,LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,10)})}}
 private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
}
