package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CustomerActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var list: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customers)
        db=DbHelper(this); list=findViewById(R.id.customerList)
        findViewById<Button>(R.id.btnSaveCustomer).setOnClickListener {
            val name=findViewById<EditText>(R.id.etCustomerName).text.toString().trim()
            val phone=findViewById<EditText>(R.id.etCustomerPhone).text.toString().trim()
            val address=findViewById<EditText>(R.id.etCustomerAddress).text.toString().trim()
            val limit=findViewById<EditText>(R.id.etCreditLimit).text.toString().toDoubleOrNull() ?: 0.0
            if(name.isBlank()){Toast.makeText(this,"Enter customer name",Toast.LENGTH_SHORT).show();return@setOnClickListener}
            db.upsertCustomer(name,phone,address,limit); render()
        }
        render()
    }
    private fun render(){
        list.removeAllViews()
        db.customerRows().forEach{r->
            list.addView(TextView(this).apply{
                text="${r[1]}\n${r[2]}\nCredit limit: Rs ${r[4]}"
                textSize=15f; setPadding(16,16,16,16); setBackgroundResource(R.drawable.feature_card)
            },LinearLayout.LayoutParams(-1,-2).apply{setMargins(0,0,0,10)})
        }
    }
}
