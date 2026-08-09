package com.rkmobiles.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db=DbHelper(this)
        tvSummary=findViewById(R.id.tvSummary)

        findViewById<Button>(R.id.btnNewSale).setOnClickListener { startActivity(Intent(this,InvoiceActivity::class.java)) }
        findViewById<Button>(R.id.btnRepair).setOnClickListener { startActivity(Intent(this,RepairActivity::class.java)) }
        findViewById<Button>(R.id.btnStock).setOnClickListener { startActivity(Intent(this,StockActivity::class.java)) }
        findViewById<Button>(R.id.btnCustomers).setOnClickListener { startActivity(Intent(this,CustomerActivity::class.java)) }
        findViewById<Button>(R.id.btnPending).setOnClickListener { startActivity(Intent(this,PendingActivity::class.java)) }
        findViewById<Button>(R.id.btnMore).setOnClickListener {
            startActivity(Intent(this, FeatureCenterActivity::class.java))
        }
        findViewById<Button>(R.id.btnPurchase).setOnClickListener {
            startActivity(Intent(this, PurchaseActivity::class.java))
        }
        findViewById<Button>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(this, BusinessReportsActivity::class.java))
        }
        findViewById<Button>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, InvoiceHistoryActivity::class.java))
        }
        refresh()
    }

    override fun onResume() { super.onResume(); if (::db.isInitialized) refresh() }

    private fun refresh() {
        val sales=db.salesTotal()
        val profit=sales-db.costTotal()-db.expenseTotal()+db.repairProfit()
        tvSummary.text="₹ ${money(sales)}\nSales this period"
        findViewById<TextView>(R.id.tvStock).text=db.stockCount().toString()
        findViewById<TextView>(R.id.tvPending).text="₹ ${money(db.pendingTotal())}"
        findViewById<TextView>(R.id.tvProfit).text="₹ ${money(profit)}"
        findViewById<TextView>(R.id.tvRepairs).text=db.repairCount().toString()
        findViewById<TextView>(R.id.tvRepairCount).text=db.repairCount().toString()
    }

    private fun money(v:Double)=String.format("%,.2f",v)
}
