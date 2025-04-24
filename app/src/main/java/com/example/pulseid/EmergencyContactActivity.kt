package com.example.pulseid

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseid.model.EmergencyContact
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EmergencyContactActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etRelation: EditText
    private lateinit var btnAdd: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EmergencyContactAdapter

    private val contactList = mutableListOf<EmergencyContact>()
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergancy_card)

        etName = findViewById(R.id.etContactName)
        etPhone = findViewById(R.id.etContactPhone)
        etRelation = findViewById(R.id.etContactRelation)
        btnAdd = findViewById(R.id.btnAddContact)
        recyclerView = findViewById(R.id.recyclerViewContacts)

        adapter = EmergencyContactAdapter(contactList) { position -> deleteContact(position) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().getReference("users/$userId/emergencyContacts")

        loadContacts()

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val relation = etRelation.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || relation.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contact = EmergencyContact(name, phone, relation)
            contactList.add(contact)
            adapter.notifyItemInserted(contactList.size - 1)
            database.setValue(contactList)

            etName.setText("")
            etPhone.setText("")
            etRelation.setText("")
        }
    }

    private fun loadContacts() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()
                for (child in snapshot.children) {
                    val contact = child.getValue(EmergencyContact::class.java)
                    contact?.let { contactList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EmergencyContactActivity, "Error loading contacts", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteContact(position: Int) {
        contactList.removeAt(position)
        adapter.notifyItemRemoved(position)
        database.setValue(contactList)
    }
}
