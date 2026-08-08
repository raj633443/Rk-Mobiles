package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class InvoiceActivity : AppCompatActivity() {
    private lateinit var db: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        db = DbHelper(this)

        findViewById<Button>(R.id.btnCreateInvoice).setOnClickListener {
            val customer = findViewById<EditText>(R.id.etInvoiceCustomer).text.toString().trim()
            val description = findViewById<EditText>(R.id.etInvoiceItem).text.toString().trim()
            val qty = findViewById<EditText>(R.id.etInvoiceQty).text.toString().toIntOrNull() ?: 0
            val rate = findViewById<EditText>(R.id.etInvoiceRate).text.toString().toDoubleOrNull()
            val cost = findViewById<EditText>(R.id.etInvoiceCost).text.toString().toDoubleOrNull() ?: 0.0
            val discount = findViewById<EditText>(R.id.etInvoiceDiscount).text.toString().toDoubleOrNull() ?: 0.0
            val paid = findViewById<EditText>(R.id.etInvoicePaid).text.toString().toDoubleOrNull() ?: 0.0
            val mode = findViewById<Spinner>(R.id.spPaymentMode).selectedItem.toString()

            if (customer.isBlank() || description.isBlank() || qty <= 0 || rate == null) {
                Toast.makeText(this, "Enter customer, item, quantity and rate", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val subtotal = qty * rate
            val invoiceNo = "RK-${System.currentTimeMillis()}"
            val id = db.createInvoice(invoiceNo, null, customer, subtotal, discount, paid, mode)
            db.addInvoiceItem(id, description, qty, rate, cost)

            Toast.makeText(this, "Invoice $invoiceNo saved", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
