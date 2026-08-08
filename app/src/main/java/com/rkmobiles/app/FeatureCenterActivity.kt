package com.rkmobiles.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FeatureCenterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feature_center)

        findViewById<Button>(R.id.btnFeatureSettings).setOnClickListener {
            startActivity(Intent(this, BusinessSettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnFeatureReports).setOnClickListener {
            startActivity(Intent(this, BusinessReportsActivity::class.java))
        }
    }

    private fun addFeature(
        container: LinearLayout,
        title: String,
        description: String
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 16, 18, 16)
            setBackgroundResource(R.drawable.feature_card)
        }

        card.addView(TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(0xFF111827.toInt())
            setTypeface(null, android.graphics.Typeface.BOLD)
        })

        card.addView(TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(0xFF6B7280.toInt())
            setPadding(0, 5, 0, 0)
        })

        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(0, 0, 0, 10)
        container.addView(card, lp)
    }
}
