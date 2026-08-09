package com.rkmobiles.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FeatureCenterActivity:AppCompatActivity(){
 override fun onCreate(b:Bundle?){super.onCreate(b);setContentView(R.layout.activity_feature_center)
  go(R.id.btnCreateInvoice,InvoiceActivity::class.java);go(R.id.btnInvoiceHistory,InvoiceHistoryActivity::class.java)
  go(R.id.btnCustomers,CustomerActivity::class.java);go(R.id.btnLedger,CustomerLedgerActivity::class.java)
  go(R.id.btnPurchase,PurchaseActivity::class.java);go(R.id.btnImeiStock,ImeiStockActivity::class.java)
  go(R.id.btnStockReport,StockReportActivity::class.java);go(R.id.btnFeatureReports,BusinessReportsActivity::class.java)
  go(R.id.btnBackup,BackupActivity::class.java);go(R.id.btnFeatureSettings,BusinessSettingsActivity::class.java)
  go(R.id.btnRepairs,RepairActivity::class.java);go(R.id.btnExpenses,ExpenseActivity::class.java)
  go(R.id.btnProfitLoss,ProfitLossActivity::class.java);go(R.id.btnPending,PendingActivity::class.java)
  go(R.id.btnStock,StockActivity::class.java)
 }
 private fun go(id:Int,c:Class<*>){findViewById<Button>(id).setOnClickListener{startActivity(Intent(this,c))}}
}
