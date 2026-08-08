package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImeiStockActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var list: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imei_stock)
        db = DbHelper(this)
        list = findViewById(R.id.imeiResults)

        findViewById<Button>(R.id.btnSearchImei).setOnClickListener {
            render(findViewById<EditText>(R.id.etImeiSearch).text.toString().trim())
        }
    }

    private fun render(query: String) {
        list.removeAllViews()
        db.searchImei(query).forEach { r ->
            val t = TextView(this).apply {
                text = "${r[1]} ${r[2]}\nIMEI: ${r[3]}\nQty: ${r[4]}  •  Buy: Rs ${r[5]}  •  Sell: Rs ${r[6]}"
                textSize = 14f
                setTextColor(0xFF172033.toInt())
                setPadding(16, 16, 16, 16)
                setBackgroundResource(R.drawable.feature_card)
            }
            val lp = LinearLayout.LayoutParams(-1, -2)
            lp.setMargins(0, 0, 0, 10)
            list.addView(t, lp)
        }
    }
}
