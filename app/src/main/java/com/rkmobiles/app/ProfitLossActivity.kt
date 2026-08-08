package com.rkmobiles.app
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
class ProfitLossActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_profit_loss)
  val db=DbHelper(this).readableDatabase;db.rawQuery("SELECT IFNULL(SUM(total),0),IFNULL(SUM(expense),0) FROM repairs",null).use{r->
   r.moveToFirst();val income=r.getDouble(0);val repair=r.getDouble(1)
   db.rawQuery("SELECT IFNULL(SUM(amount),0) FROM expenses",null).use{e->{e.moveToFirst();val other=e.getDouble(0);findViewById<TextView>(R.id.tvReport).text="Repair Income: ₹${income.toInt()}\nRepair Expenses: ₹${repair.toInt()}\nOther Expenses: ₹${other.toInt()}\n\nNet Profit: ₹${(income-repair-other).toInt()}"}}}
 }
}
