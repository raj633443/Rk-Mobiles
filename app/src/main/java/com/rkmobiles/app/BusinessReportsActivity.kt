package com.rkmobiles.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BusinessReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_reports)
        val db=DbHelper(this)
        val net=db.salesTotal()-db.costTotal()-db.expenseTotal()+db.repairProfit()
        findViewById<TextView>(R.id.reportBody).text =
            "Sales: Rs ${f(db.salesTotal())}\n" +
            "Sales Cost: Rs ${f(db.costTotal())}\n" +
            "Expenses: Rs ${f(db.expenseTotal())}\n" +
            "Repair Profit: Rs ${f(db.repairProfit())}\n" +
            "Receivables: Rs ${f(db.pendingTotal())}\n\n" +
            "Net Profit: Rs ${f(net)}"
    }
    private fun f(v:Double)=String.format("%.2f",v)
}
