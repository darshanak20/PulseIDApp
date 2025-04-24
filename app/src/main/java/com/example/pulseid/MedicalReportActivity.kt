package com.example.pulseid

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseid.model.Report
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import com.example.pulseid.adapter.ReportAdapter

private lateinit var storageRef: StorageReference
private lateinit var database: DatabaseReference

class MedicalReportActivity: AppCompatActivity() {

    private lateinit var reportList: MutableList<Report>
    private lateinit var database: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var auth: FirebaseAuth
    private val PICK_FILE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_report)

        storageRef = FirebaseStorage.getInstance().reference.child("reports")

      //  database = FirebaseDatabase.getInstance().getReference("userReports").child(userID)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().getReference("users/$userId/reports")
        storageRef = FirebaseStorage.getInstance().getReference("users/$userId/reports")

        reportList = mutableListOf()
        recyclerView = findViewById(R.id.recyclerReportList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ReportAdapter(reportList, ::deleteReport)
        recyclerView.adapter = adapter

        val btnAddReport = findViewById<Button>(R.id.btnAddReport)
        btnAddReport.setOnClickListener {
            pickFile()
        }

        loadReports()
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    private fun loadReports() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reportList.clear()
                for (reportSnap in snapshot.children) {
                    val report = reportSnap.getValue(Report::class.java)
                    report?.let { reportList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MedicalReportActivity, "Error loading reports", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteReport(report: Report) {
        storageRef.child(report.fileName).delete().addOnSuccessListener {
            database.orderByChild("name").equalTo(report.fileName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            child.ref.removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val fileUri = data.data
            fileUri?.let { uploadFile(it) }
        }
    }

    private fun uploadFile(uri: Uri) {
        val fileName = UUID.randomUUID().toString()
        val fileRef = storageRef.child(fileName)

        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                val report = Report(fileName, downloadUri.toString())
                database.push().setValue(report)
                Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }
}
