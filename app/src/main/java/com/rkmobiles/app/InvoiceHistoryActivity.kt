package com.rkmobiles.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InvoiceHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_history)
        val list = findViewById<LinearLayout>(R.id.invoiceHistoryList)
        DbHelper(this).invoiceRows().forEach { r ->
            val t = TextView(this).apply {
                text = "${r[1]}  •  ${r[2]}\nTotal: Rs ${r[3]}  •  Paid: Rs ${r[4]}\n${r[5]}"
                textSize = 15f
                setTextColor(0xFF172033.toInt())
                setPadding(16,16,16,16)
                setBackgroundResource(R.drawable.feature_card)
            }
            list.addView(t, LinearLayout.LayoutParams(-1,-2).apply { setMargins(0,0,0,10) })
        }
    }
}
