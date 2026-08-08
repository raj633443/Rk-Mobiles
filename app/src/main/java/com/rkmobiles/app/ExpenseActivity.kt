package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_expense)
        findViewById<Button>(R.id.btnSaveExpense).setOnClickListener { val d=findViewById<EditText>(R.id.etExpenseDesc).text.toString(); val a=findViewById<EditText>(R.id.etExpenseAmount).text.toString().toDoubleOrNull(); if(d.isBlank()||a==null){Toast.makeText(this,"Enter expense details",Toast.LENGTH_SHORT).show();return@setOnClickListener}; DbHelper(this).insertExpense(d,a); Toast.makeText(this,"Expense saved",Toast.LENGTH_SHORT).show(); finish() }
    }
}
