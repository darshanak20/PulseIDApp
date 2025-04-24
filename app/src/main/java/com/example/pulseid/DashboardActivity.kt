package com.example.pulseid

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.pulseid.R.id.scrollView

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val cardEmergencyContact = findViewById<CardView>(R.id.cardEmergencyContact)
        val cardNearbyHospital = findViewById<CardView>(R.id.cardNearbyHospital)
        //val cardMedicalReport = findViewById<CardView>(R.id.cardMedicalReport)
        //val cardBasicInfo = findViewById<CardView>(R.id.cardBasicInfo)

// Open Emergency Contact Page
        cardEmergencyContact.setOnClickListener {
            startActivity(Intent(this, EmergencyContactActivity::class.java))
        }

// Open Nearby Hospital Page
        cardNearbyHospital.setOnClickListener {
            startActivity(Intent(this, NearbyHospitalActivity::class.java))
        }

// Open Personal Medical Report Page
    /*    cardMedicalReport.setOnClickListener {
            startActivity(Intent(this, MedicalReportActivity::class.java))
        }

// Open Detailed Medical Info Page
        cardBasicInfo.setOnClickListener {
            startActivity(Intent(this, AddMedicalInfoActivity::class.java))
        } */



    }
    }
