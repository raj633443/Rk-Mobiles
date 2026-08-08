package com.rkmobiles.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PendingActivity : AppCompatActivity() {
    private lateinit var db: DbHelper
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending)
        db = DbHelper(this)
        container = findViewById(R.id.pendingContainer)
        render()
    }

    private fun render() {
        container.removeAllViews()
        val rows = db.pendingRows()

        if (rows.isEmpty()) {
            val t = TextView(this).apply {
                text = "No pending payments"
                textSize = 18f
            }
            container.addView(t)
            return
        }

        rows.forEach { r ->
            val t = TextView(this).apply {
                text = "${r[1]}  •  Rs ${r[3]}\n${r[2]}  •  Due: ${r[4]}"
                textSize = 17f
                setPadding(12, 16, 12, 16)
                setOnClickListener {
                    AlertDialog.Builder(this@PendingActivity)
                        .setTitle("Pending Payment")
                        .setMessage("${r[1]}\nRs ${r[3]}")
                        .setPositiveButton("WhatsApp") { _, _ ->
                            whatsapp(
                                r[2],
                                "Hi ${r[1]}, this is a reminder from RK Mobiles. Pending amount: Rs ${r[3]}. Thank you."
                            )
                        }
                        .setNegativeButton("Mark Paid") { _, _ ->
                            db.markPendingPaid(r[0].toLong())
                            render()
                        }
                        .setNeutralButton("Close", null)
                        .show()
                }
            }
            container.addView(t)
        }
    }

    private fun whatsapp(phone: String, message: String) {
        if (phone.isBlank()) {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = Uri.parse(
            "https://wa.me/${phone.replace("+", "")}?text=${Uri.encode(message)}"
        )
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
