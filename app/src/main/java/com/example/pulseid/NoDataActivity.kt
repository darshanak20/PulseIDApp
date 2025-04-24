package com.example.pulseid

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class NoDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_data)

        val addButton = findViewById<Button>(R.id.btnAddMedicalInfo)
        addButton.setOnClickListener {
            // Open input screen (or just show Toast for now)
            Toast.makeText(this, "Add Medical Info clicked", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, AddMedicalInfoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
