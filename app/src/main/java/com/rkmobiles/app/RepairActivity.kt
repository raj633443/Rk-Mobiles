package com.rkmobiles.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RepairActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_repair)
        findViewById<Button>(R.id.btnAddRepair).setOnClickListener { startActivity(Intent(this, AddRepairActivity::class.java)) }
    }
}
