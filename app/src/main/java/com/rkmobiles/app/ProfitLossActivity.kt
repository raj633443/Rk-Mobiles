package com.rkmobiles.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfitLossActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profit_loss)

        val db = DbHelper(this)
        val sales = db.salesTotal()
        val cost = db.costTotal()
        val expenses = db.expenseTotal()
        val repair = db.repairProfit()
        val net = sales - cost - expenses + repair

        findViewById<TextView>(R.id.tvPL).text =
            "Sales\n₹ ${money(sales)}\n\n" +
            "Sales Cost\n₹ ${money(cost)}\n\n" +
            "Expenses\n₹ ${money(expenses)}\n\n" +
            "Repair Profit\n₹ ${money(repair)}\n\n" +
            "NET PROFIT\n₹ ${money(net)}"
    }

    private fun money(value: Double): String = String.format("%,.2f", value)
}
