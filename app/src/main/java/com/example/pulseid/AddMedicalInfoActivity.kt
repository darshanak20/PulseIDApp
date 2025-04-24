package com.example.pulseid

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseid.model.MedicalInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddMedicalInfoActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etAge: EditText
    private lateinit var etBloodGroup: EditText
    private lateinit var etAllergies: EditText
    private lateinit var etConditions: EditText
    private lateinit var etMedications: EditText
    private lateinit var etDonor: EditText
    private lateinit var etInsuranceId: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var btnSave: Button

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medical_info)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().getReference("users/$userId/medicalInfo")

        initViews()
        loadMedicalInfo()

        btnSave.setOnClickListener {
            saveMedicalInfo()
        }
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etAge = findViewById(R.id.etAge)
        etBloodGroup = findViewById(R.id.etBloodGroup)
        etAllergies = findViewById(R.id.etAllergies)
        etConditions = findViewById(R.id.etConditions)
        etMedications = findViewById(R.id.etMedications)
        etDonor = findViewById(R.id.etDonor)
        etInsuranceId = findViewById(R.id.etInsuranceId)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun loadMedicalInfo() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    etFullName.setText(snapshot.child("fullName").getValue(String::class.java))
                    etAge.setText(snapshot.child("age").getValue(String::class.java))
                    etBloodGroup.setText(snapshot.child("bloodGroup").getValue(String::class.java))
                    etAllergies.setText(snapshot.child("allergies").getValue(String::class.java))
                    etConditions.setText(snapshot.child("conditions").getValue(String::class.java))
                    etMedications.setText(snapshot.child("medications").getValue(String::class.java))
                    etDonor.setText(snapshot.child("organDonor").getValue(String::class.java))
                    etInsuranceId.setText(snapshot.child("insuranceId").getValue(String::class.java))
                    etHeight.setText(snapshot.child("height").getValue(String::class.java))
                    etWeight.setText(snapshot.child("weight").getValue(String::class.java))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddMedicalInfoActivity, "Error loading data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveMedicalInfo() {
        val info = MedicalInfo(
            etFullName.text.toString().trim(),
            etAge.text.toString().trim(),
            etBloodGroup.text.toString().trim(),
            etAllergies.text.toString().trim(),
            etConditions.text.toString().trim(),
            etMedications.text.toString().trim(),
            etDonor.text.toString().trim(),
            etInsuranceId.text.toString().trim(),
            etHeight.text.toString().trim(),
            etWeight.text.toString().trim()
        )

        if (info.fullName.isEmpty() || info.age.isEmpty() || info.bloodGroup.isEmpty()) {
            Toast.makeText(this, "Name, Age, and Blood Group are required", Toast.LENGTH_SHORT).show()
            return
        }

        database.setValue(info).addOnSuccessListener {
            Toast.makeText(this, "Information saved", Toast.LENGTH_SHORT).show()


            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()

        }.addOnFailureListener {
            Toast.makeText(this, "Failed to save info: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
