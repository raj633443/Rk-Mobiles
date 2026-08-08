package com.rkmobiles.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfitLossActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_profit_loss); refresh()
    }
    private fun refresh(){ val db=DbHelper(this); val sales=db.salesTotal(); val cost=db.costTotal(); val expenses=db.expenseTotal(); val repair=db.repairProfit(); findViewById<TextView>(R.id.tvPL).text="Sales: Rs ${f(sales)}
Sales Cost: Rs ${f(cost)}
Expenses: Rs ${f(expenses)}
Repair Profit: Rs ${f(repair)}

Net Profit: Rs ${f(sales-cost-expenses+repair)}" }
    private fun f(v:Double)=String.format("%.2f",v)
}
