package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_sale)
        findViewById<Button>(R.id.btnSaveSale).setOnClickListener {
            val customer=findViewById<EditText>(R.id.etSaleCustomer).text.toString(); val desc=findViewById<EditText>(R.id.etSaleDescription).text.toString(); val amount=findViewById<EditText>(R.id.etSaleAmount).text.toString().toDoubleOrNull(); val cost=findViewById<EditText>(R.id.etSaleCost).text.toString().toDoubleOrNull()
            if(amount==null || cost==null){Toast.makeText(this,"Enter sale and cost",Toast.LENGTH_SHORT).show();return@setOnClickListener}; DbHelper(this).insertSale(customer,desc,amount,cost); Toast.makeText(this,"Sale saved",Toast.LENGTH_SHORT).show(); finish()
        }
    }
}
