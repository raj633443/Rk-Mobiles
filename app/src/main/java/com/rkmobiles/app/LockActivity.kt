package com.rkmobiles.app
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LockActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        val pin = findViewById<EditText>(R.id.etPin)
        findViewById<Button>(R.id.btnUnlock).setOnClickListener {
            if (pin.text.toString() == SessionManager(this).getPin()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
        }
    }
}
