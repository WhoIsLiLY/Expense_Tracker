package com.ubaya.expensetracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.expensetracker.databinding.ReportItemLayoutBinding
import com.ubaya.expensetracker.model.ReportData
import java.text.NumberFormat
import java.util.Locale

class ListReportAdapter(private var reportList: List<ReportData>) :
    RecyclerView.Adapter<ListReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ReportItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        // Gunakan nama binding yang sesuai dengan file XML Anda
        val binding = ReportItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun getItemCount() = reportList.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val currentReport = reportList[position]
        with(holder.binding) {
            val totalExpense = currentReport.totalExpense ?: 0
            val budgetNominal = currentReport.budgetNominal
            val budgetLeft = budgetNominal - totalExpense

            // Format angka ke format Rupiah
            val nf = NumberFormat.getNumberInstance(Locale("in", "ID"))

            textBudgetTitle.text = currentReport.budgetName
            textBudgetUsed.text = "IDR ${nf.format(totalExpense)}"
            textBudgetMax.text = "IDR ${nf.format(budgetNominal)}"
            textBudgetLeft.text = "Budget left: IDR ${nf.format(budgetLeft)}"

            // Hitung persentase untuk progress bar
            if (budgetNominal > 0) {
                progressBarReport.progress = (totalExpense.toLong() * 100 / budgetNominal).toInt()
            } else {
                progressBarReport.progress = 0
            }
        }
    }

    fun updateReportList(newReportList: List<ReportData>) {
        reportList = newReportList
        notifyDataSetChanged()
    }
}
