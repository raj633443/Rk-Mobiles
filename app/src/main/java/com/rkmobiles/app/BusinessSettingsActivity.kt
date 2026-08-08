package com.rkmobiles.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BusinessSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_settings)
        val db=DbHelper(this); val p=db.firmProfile()
        findViewById<EditText>(R.id.etFirmName).setText(p[0])
        findViewById<EditText>(R.id.etFirmPhone).setText(p[1])
        findViewById<EditText>(R.id.etFirmAddress).setText(p[2])
        findViewById<EditText>(R.id.etFirmGstin).setText(p[3])
        findViewById<EditText>(R.id.etInvoicePrefix).setText(p[4])
        findViewById<Button>(R.id.btnSaveFirm).setOnClickListener {
            val name=findViewById<EditText>(R.id.etFirmName).text.toString().trim()
            if(name.isBlank()){Toast.makeText(this,"Enter business name",Toast.LENGTH_SHORT).show();return@setOnClickListener}
            db.setFirmProfile(name,
                findViewById<EditText>(R.id.etFirmPhone).text.toString().trim(),
                findViewById<EditText>(R.id.etFirmAddress).text.toString().trim(),
                findViewById<EditText>(R.id.etFirmGstin).text.toString().trim(),
                findViewById<EditText>(R.id.etInvoicePrefix).text.toString().trim().ifBlank{"RK"})
            Toast.makeText(this,"Business profile saved",Toast.LENGTH_SHORT).show()
        }
    }
}
