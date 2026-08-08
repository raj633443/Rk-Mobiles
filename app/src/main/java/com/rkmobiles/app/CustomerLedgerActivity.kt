package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomerLedgerActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var list: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_ledger)
        db = DbHelper(this)
        list = findViewById(R.id.ledgerList)

        findViewById<Button>(R.id.btnLedgerSearch).setOnClickListener {
            val name = findViewById<EditText>(R.id.etLedgerCustomer).text.toString().trim()
            render(name)
        }

        findViewById<Button>(R.id.btnAddPayment).setOnClickListener {
            val name = findViewById<EditText>(R.id.etLedgerCustomer).text.toString().trim()
            val amount = findViewById<EditText>(R.id.etPaymentAmount)
                .text.toString().toDoubleOrNull()

            if (name.isBlank() || amount == null || amount <= 0) {
                Toast.makeText(this, "Enter customer and valid payment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.addPayment(null, name, amount, "Cash", "Ledger payment")
            render(name)
        }
    }

    private fun render(name: String) {
        list.removeAllViews()
        val rows = db.customerLedger(name)

        for (r in rows) {
            val card = TextView(this)
            card.text = "${r[0]}  •  ${r[1]}\nAmount: Rs ${r[2]}  •  Paid: Rs ${r[3]}"
            card.textSize = 14f
            card.setPadding(16, 16, 16, 16)
            card.setBackgroundResource(R.drawable.feature_card)

            val params = LinearLayout.LayoutParams(-1, -2)
            params.setMargins(0, 0, 0, 10)
            list.addView(card, params)
        }
    }
}
