package com.rkmobiles.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BusinessReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_reports)

        val db = DbHelper(this)

        findViewById<TextView>(R.id.reportBody).text = """BUSINESS REPORTS

Sales: Rs ${money(db.salesTotal())}
Sales Cost: Rs ${money(db.costTotal())}
Expenses: Rs ${money(db.expenseTotal())}
Repair Profit: Rs ${money(db.repairProfit())}
Receivables: Rs ${money(db.pendingTotal())}

Net Profit: Rs ${money(
            db.salesTotal() -
                db.costTotal() -
                db.expenseTotal() +
                db.repairProfit()
        )}

Available report modules:
• Balance Sheet
• Billwise Profit & Loss
• Partywise Profit & Loss
• Item Batch & Serial
• Invoice Profit Check"""
    }

    private fun money(v: Double) = String.format("%.2f", v)
}
