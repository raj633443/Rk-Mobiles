package com.rkmobiles.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class RepairActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repair)
        db = DbHelper(this)
        container = findViewById(R.id.repairContainer)

        findViewById<Button>(R.id.btnAddRepair).setOnClickListener {
            startActivity(Intent(this, AddRepairActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        renderRepairs()
    }

    private fun renderRepairs() {
        container.removeAllViews()
        val rows = db.repairRows()

        if (rows.isEmpty()) {
            container.addView(TextView(this).apply {
                text = "No repairs yet\nTap + Add New Repair to create one."
                textSize = 16f
                setTextColor(0xFF6B7280.toInt())
                setPadding(18, 22, 18, 22)
            })
            return
        }

        rows.forEach { r ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(18, 16, 18, 16)
                setBackgroundResource(R.drawable.card_bg)
            }
            card.addView(TextView(this).apply {
                text = "${r[1]}  •  ${r[3]}"
                textSize = 18f
                setTextColor(0xFF111827.toInt())
            })
            card.addView(TextView(this).apply {
                text = "${r[4]}\nCharge: Rs ${r[6]}   Cost: Rs ${r[5]}\nStatus: ${r[7]}"
                textSize = 14f
                setTextColor(0xFF6B7280.toInt())
                setPadding(0, 8, 0, 8)
            })
            card.addView(Button(this).apply {
                text = "Change Status"
                setOnClickListener { showStatusDialog(r[0].toLong()) }
            })

            val lp = LinearLayout.LayoutParams(-1, -2)
            lp.setMargins(0, 0, 0, 12)
            container.addView(card, lp)
        }
    }

    private fun showStatusDialog(id: Long) {
        val statuses = arrayOf("Pending", "Checking", "Repairing", "Ready", "Delivered")
        AlertDialog.Builder(this)
            .setTitle("Repair Status")
            .setItems(statuses) { _, which ->
                db.updateRepairStatus(id, statuses[which])
                renderRepairs()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
