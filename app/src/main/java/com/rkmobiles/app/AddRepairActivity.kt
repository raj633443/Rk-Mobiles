package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddRepairActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_add_repair)
        findViewById<Button>(R.id.btnSaveRepair).setOnClickListener {
            val customer=findViewById<EditText>(R.id.etCustomer).text.toString().trim(); val phone=findViewById<EditText>(R.id.etPhone).text.toString().trim(); val model=findViewById<EditText>(R.id.etRepairModel).text.toString().trim(); val issue=findViewById<EditText>(R.id.etIssue).text.toString().trim(); val cost=findViewById<EditText>(R.id.etRepairCost).text.toString().toDoubleOrNull(); val charge=findViewById<EditText>(R.id.etRepairCharge).text.toString().toDoubleOrNull()
            if(customer.isEmpty() || cost==null || charge==null){Toast.makeText(this,"Enter customer, cost and charge",Toast.LENGTH_SHORT).show();return@setOnClickListener}
            DbHelper(this).insertRepair(customer,phone,model,issue,cost,charge); Toast.makeText(this,"Repair saved",Toast.LENGTH_SHORT).show(); finish()
        }
    }
}
