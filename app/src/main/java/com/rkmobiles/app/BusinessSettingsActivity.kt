package com.rkmobiles.app

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class BusinessSettingsActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("business_settings", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_settings)
        val box = findViewById<LinearLayout>(R.id.settingsContainer)
        val keys = listOf(
            "Sync data across devices", "Multiple companies / firms", "E-way Bills", "Restore deleted transactions",
            "Multiple item pricing", "Bulk item update", "Party credit limits", "Fixed assets",
            "Automated payment reminders", "Accounting module", "WhatsApp Connect", "TCS on invoices",
            "Different rates per party", "Profit on invoices", "Expenses with input tax credit",
            "Custom item fields", "Transaction message to self", "Transaction update messages", "TDS on invoices",
            "Service reminders"
        )
        keys.forEach { label ->
            val key = "f_" + label.lowercase().replace(Regex("[^a-z0-9]+"), "_")
            val sw = Switch(this).apply {
                text = label
                textSize = 15f
                setPadding(8, 12, 8, 12)
                isChecked = prefs.getBoolean(key, false)
                setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean(key, checked).apply() }
            }
            box.addView(sw)
        }
    }
}
