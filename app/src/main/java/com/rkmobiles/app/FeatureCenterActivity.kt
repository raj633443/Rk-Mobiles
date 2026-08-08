package com.rkmobiles.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FeatureCenterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_center)

        findViewById<Button>(R.id.btnCreateInvoice).setOnClickListener { startActivity(Intent(this, InvoiceActivity::class.java)) }
        findViewById<Button>(R.id.btnInvoiceHistory).setOnClickListener { startActivity(Intent(this, InvoiceHistoryActivity::class.java)) }
        findViewById<Button>(R.id.btnCustomers).setOnClickListener { startActivity(Intent(this, CustomerActivity::class.java)) }
        findViewById<Button>(R.id.btnLedger).setOnClickListener { startActivity(Intent(this, CustomerLedgerActivity::class.java)) }
        findViewById<Button>(R.id.btnPurchase).setOnClickListener { startActivity(Intent(this, PurchaseActivity::class.java)) }
        findViewById<Button>(R.id.btnImeiStock).setOnClickListener { startActivity(Intent(this, ImeiStockActivity::class.java)) }
        findViewById<Button>(R.id.btnStockReport).setOnClickListener { startActivity(Intent(this, StockReportActivity::class.java)) }
        findViewById<Button>(R.id.btnBackup).setOnClickListener { startActivity(Intent(this, BackupActivity::class.java)) }
        findViewById<Button>(R.id.btnFeatureReports).setOnClickListener { startActivity(Intent(this, BusinessReportsActivity::class.java)) }
        findViewById<Button>(R.id.btnFeatureSettings).setOnClickListener { startActivity(Intent(this, BusinessSettingsActivity::class.java)) }
    }
}
