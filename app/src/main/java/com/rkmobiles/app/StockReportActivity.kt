package com.rkmobiles.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StockReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_report)
        val db = DbHelper(this)
        val low = db.lowStockRows()
        findViewById<TextView>(R.id.stockReportBody).text =
            "Total stock: ${db.stockCount()} units\n\n" +
            if (low.isEmpty()) "No low-stock items." else
                low.joinToString("\n") { "${it[1]} ${it[2]} — Qty ${it[3]} — IMEI ${it[4]}" }
    }
}
