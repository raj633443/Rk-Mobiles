package com.rkmobiles.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var summary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DbHelper(this)
        summary = findViewById(R.id.tvSummary)

        findViewById<Button>(R.id.btnStock).setOnClickListener {
            startActivity(Intent(this, StockActivity::class.java))
        }
        findViewById<Button>(R.id.btnRepair).setOnClickListener {
            startActivity(Intent(this, RepairActivity::class.java))
        }
        findViewById<Button>(R.id.btnPending).setOnClickListener {
            startActivity(Intent(this, PendingActivity::class.java))
        }
        findViewById<Button>(R.id.btnSale).setOnClickListener {
            startActivity(Intent(this, SaleActivity::class.java))
        }
        findViewById<Button>(R.id.btnExpense).setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }
        findViewById<Button>(R.id.btnProfit).setOnClickListener {
            startActivity(Intent(this, ProfitLossActivity::class.java))
        }
        findViewById<Button>(R.id.btnMoreFeatures).setOnClickListener {
            startActivity(Intent(this, FeatureCenterActivity::class.java))
        }

        ReminderScheduler.schedule(this)

        if (Build.VERSION.SDK_INT >= 33 &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                20
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (::db.isInitialized) refresh()
    }

    private fun refresh() {
        summary.text =
            "Stock: ${db.stockCount()} units\n" +
            "Pending: Rs ${money(db.pendingTotal())}\n" +
            "Sales: Rs ${money(db.salesTotal())}\n" +
            "Profit: Rs ${money(
                db.salesTotal() -
                    db.costTotal() -
                    db.expenseTotal() +
                    db.repairProfit()
            )}"
    }

    private fun money(v: Double) = String.format("%.2f", v)
}
