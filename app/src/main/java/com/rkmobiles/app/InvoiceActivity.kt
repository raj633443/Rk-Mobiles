package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

open class InvoiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)
        val db = DbHelper(this)

        findViewById<Button>(R.id.btnCreateInvoice).setOnClickListener {
            val customer = findViewById<EditText>(R.id.etInvoiceCustomer).text.toString().trim()
            val item = findViewById<EditText>(R.id.etInvoiceItem).text.toString().trim()
            val qty = findViewById<EditText>(R.id.etInvoiceQty).text.toString().toIntOrNull()
            val rate = findViewById<EditText>(R.id.etInvoiceRate).text.toString().toDoubleOrNull()
            val cost = findViewById<EditText>(R.id.etInvoiceCost).text.toString().toDoubleOrNull() ?: 0.0
            val discount = findViewById<EditText>(R.id.etInvoiceDiscount).text.toString().toDoubleOrNull() ?: 0.0
            val paid = findViewById<EditText>(R.id.etInvoicePaid).text.toString().toDoubleOrNull() ?: 0.0
            val mode = findViewById<Spinner>(R.id.spPaymentMode).selectedItem.toString()

            if (customer.isBlank() || item.isBlank() || qty == null || qty <= 0 || rate == null || rate < 0) {
                Toast.makeText(this, "Enter valid invoice details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = (qty * rate - discount).coerceAtLeast(0.0)
            if (paid < 0.0 || paid > total) {
                Toast.makeText(this, "Paid amount must be between 0 and invoice total", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.saveInvoiceAndSync(
                "RK-${System.currentTimeMillis()}",
                customer, item, qty, rate, cost, discount, paid, mode
            )
            Toast.makeText(this, "Invoice saved • Stock/Sales/Pending updated", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
