package com.rkmobiles.app

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnAddRepair).setOnClickListener { startActivity(Intent(this, AddRepairActivity::class.java)) }
        findViewById<Button>(R.id.btnPending).setOnClickListener { startActivity(Intent(this, PendingActivity::class.java)) }
        findViewById<Button>(R.id.btnStock).setOnClickListener { startActivity(Intent(this, StockActivity::class.java)) }
        findViewById<Button>(R.id.btnExpense).setOnClickListener { startActivity(Intent(this, ExpenseActivity::class.java)) }
        findViewById<Button>(R.id.btnProfitLoss).setOnClickListener { startActivity(Intent(this, ProfitLossActivity::class.java)) }
        findViewById<Button>(R.id.btnSettings).setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
        ReminderScheduler.schedule(this)
    }
    override fun onResume() { super.onResume(); loadDashboard() }
    private fun loadDashboard() {
        val db = DbHelper(this).readableDatabase
        db.rawQuery("SELECT IFNULL(SUM(total-expense),0), IFNULL(SUM(pending),0), COUNT(CASE WHEN pending>0 THEN 1 END) FROM repairs", null).use {
            if (it.moveToFirst()) {
                findViewById<TextView>(R.id.tvProfit).text = "₹${it.getDouble(0).toInt()}"
                findViewById<TextView>(R.id.tvPending).text = "₹${it.getDouble(1).toInt()}"
                findViewById<TextView>(R.id.tvRepairs).text = "Pending Repairs: ${it.getInt(2)}"
            }
        }
        db.rawQuery("SELECT COUNT(*) FROM stock WHERE qty<=5", null).use {
            if (it.moveToFirst()) findViewById<TextView>(R.id.tvLowStock).text = "Low Stock: ${it.getInt(0)}"
        }
    }
}
