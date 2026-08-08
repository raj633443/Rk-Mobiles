package com.rkmobiles.app

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class BackupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup)

        findViewById<Button>(R.id.btnBackupInfo).setOnClickListener {
            val dbFile = getDatabasePath("rk_mobiles.db")
            if (!dbFile.exists()) {
                Toast.makeText(this, "No database found yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val out = File(filesDir, "rk_mobiles_backup_${System.currentTimeMillis()}.db")
            dbFile.copyTo(out, overwrite = true)
            Toast.makeText(this, "Backup created in app storage", Toast.LENGTH_LONG).show()
        }
    }
}
