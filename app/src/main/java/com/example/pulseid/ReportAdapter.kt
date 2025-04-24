package com.example.pulseid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseid.R
import com.example.pulseid.model.Report

class ReportAdapter(
    private val reportList: List<Report>,
    private val onDeleteClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReportName: TextView = view.findViewById(R.id.tvReportName)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteReport)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_card, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.tvReportName.text = report.fileName

        holder.btnDelete.setOnClickListener {
            onDeleteClick(report)
        }
    }

    override fun getItemCount(): Int = reportList.size
}
