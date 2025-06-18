package com.ubaya.expensetracker.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.Navigation
import com.ubaya.expensetracker.databinding.BudgetItemLayoutBinding
import com.ubaya.expensetracker.model.Budget
import java.text.NumberFormat
import java.util.Locale

class ListBudgetAdapter(val budgetList:ArrayList<Budget>)
    : RecyclerView.Adapter<ListBudgetAdapter.BudgetViewHolder>() {
    class BudgetViewHolder(var binding: BudgetItemLayoutBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        var binding = BudgetItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.binding.textName.text = budgetList[position].name
        val nominal = budgetList[position].nominal
        val formattedNominal = NumberFormat.getNumberInstance(Locale("in", "ID")).format(nominal)
        holder.binding.textNominal.text = "IDR $formattedNominal"

        holder.binding.textName.setOnClickListener {
            val action = ListBudgetFragmentDirections.actionEditBudget(budgetList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return budgetList.size
    }

    fun updateBudgetList(listBudget: List<Budget>) {
        budgetList.clear()
        budgetList.addAll(listBudget)
        notifyDataSetChanged()
    }
}
