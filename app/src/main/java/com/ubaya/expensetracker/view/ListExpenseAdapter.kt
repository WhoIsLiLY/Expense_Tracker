package com.ubaya.expensetracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ubaya.expensetracker.databinding.BudgetItemLayoutBinding
import com.ubaya.expensetracker.databinding.ExpenseItemLayoutBinding
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.Expense
import com.ubaya.expensetracker.model.ExpenseWithBudget
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ubaya.expensetracker.databinding.DialogExpenseDetailBinding

class ListExpenseAdapter(val expenseList:ArrayList<ExpenseWithBudget>)
    : RecyclerView.Adapter<ListExpenseAdapter.ExpenseViewHolder>() {
    class ExpenseViewHolder(var binding: ExpenseItemLayoutBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        var binding = ExpenseItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val context = holder.itemView.context

        val sdf = SimpleDateFormat("dd MMM yyyy hh.mm a", Locale.getDefault())
        val date = Date(expenseList[position].date)
        holder.binding.textViewDateTime.text = sdf.format(date)
        holder.binding.chipCategory.text = expenseList[position].budgetName
        val nominal = expenseList[position].nominal
        val formattedNominal = NumberFormat.getNumberInstance(Locale("in", "ID")).format(nominal)
        holder.binding.txtNominalExpense.text = "IDR $formattedNominal"

        holder.binding.chipCategory.setOnClickListener {
// Inflate layout dialog kustom
            val dialogBinding = DialogExpenseDetailBinding.inflate(LayoutInflater.from(context))

            // Buat dialog menggunakan MaterialAlertDialogBuilder
            val dialog = MaterialAlertDialogBuilder(context)
                .setView(dialogBinding.root)
                .setCancelable(true) // Bisa ditutup dengan menekan di luar dialog
                .create()

            // Isi data ke dalam view di dialog
            with(dialogBinding) {
                // Format tanggal
                val sdf = SimpleDateFormat("dd MMM yyyy hh.mm a", Locale.getDefault())
                val date = Date(expenseList[position].date)
                textDetailDate.text = sdf.format(date)

                // Isi catatan
                textDetailNotes.text = expenseList[position].notes

                // Isi kategori budget
                chipDetailBudget.text = expenseList[position].budgetName

                // Format dan isi nominal
                val formattedNominal = NumberFormat.getNumberInstance(Locale("in", "ID")).format(expenseList[position].nominal)
                textDetailNominal.text = "IDR $formattedNominal"

                // Tambahkan listener ke tombol close
                buttonCloseDialog.setOnClickListener {
                    dialog.dismiss() // Tutup dialog
                }
            }

            // Tampilkan dialog
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    fun updateExpenseList(listexpense: List<ExpenseWithBudget>) {
        expenseList.clear()
        expenseList.addAll(listexpense)
        notifyDataSetChanged()
    }
}
