package com.example.pulseid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseid.model.EmergencyContact

class EmergencyContactAdapter(
    private val contacts: MutableList<EmergencyContact>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<EmergencyContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactText: TextView = view.findViewById(R.id.tvContact)
        val deleteBtn: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.contactText.text = "${contact.name} (${contact.relation})\n${contact.phone}"
        holder.deleteBtn.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = contacts.size
}
