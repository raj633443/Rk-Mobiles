package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PurchaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        findViewById<Button>(R.id.btnSavePurchase).setOnClickListener {
            val supplier = findViewById<EditText>(R.id.etSupplier).text.toString().trim()
            val item = findViewById<EditText>(R.id.etPurchaseItem).text.toString().trim()
            val qty = findViewById<EditText>(R.id.etPurchaseQty).text.toString().toIntOrNull()
            val buy = findViewById<EditText>(R.id.etPurchaseBuy).text.toString().toDoubleOrNull()
            val imei = findViewById<EditText>(R.id.etPurchaseImei).text.toString().trim()

            if (item.isBlank() || qty == null || qty <= 0 || buy == null || buy < 0) {
                Toast.makeText(this, "Enter valid purchase details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            DbHelper(this).insertPurchase(supplier, item, qty, buy, imei)
            Toast.makeText(this, "Purchase saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
