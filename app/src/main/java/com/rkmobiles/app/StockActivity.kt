package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StockActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var list: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_stock); db = DbHelper(this); list = findViewById(R.id.tvStockList)
        findViewById<Button>(R.id.btnAddStock).setOnClickListener {
            val model = findViewById<EditText>(R.id.etModel).text.toString().trim(); val qty = findViewById<EditText>(R.id.etQty).text.toString().toIntOrNull(); val buy = findViewById<EditText>(R.id.etBuy).text.toString().toDoubleOrNull(); val sell = findViewById<EditText>(R.id.etSell).text.toString().toDoubleOrNull()
            if (model.isEmpty() || qty == null || buy == null || sell == null) { Toast.makeText(this, "Enter all stock details", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            db.insertStock(model, qty, buy, sell); Toast.makeText(this, "Stock saved", Toast.LENGTH_SHORT).show(); list.text = "Total stock units: ${db.stockCount()}"
        }
    }
}
