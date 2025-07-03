package com.ubaya.expensetracker.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ubaya.expensetracker.databinding.FragmentNewExpenseBinding
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.Expense
import com.ubaya.expensetracker.viewmodel.DetailExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewExpenseFragment : Fragment() {
    private lateinit var binding: FragmentNewExpenseBinding
    private lateinit var viewModel: DetailExpenseViewModel

    private var selectedBudget: Budget? = null
    private var budgetList: List<Budget> = listOf()
    // Variabel untuk menyimpan total pengeluaran terakhir yang diterima dari ViewModel
    private var currentTotalExpense: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailExpenseViewModel::class.java)
        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPref.getInt("LOGGED_IN_USER_ID", -1)

        if (loggedInUserId != -1) {
            viewModel.refreshBudgets(loggedInUserId)
        }

        setupCurrentDate()
        observeViewModel()

        binding.buttonAddExpense.setOnClickListener {
            handleAddExpense()
        }
    }

    private fun setupCurrentDate() {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        binding.textDate.text = currentDate
    }

    private fun observeViewModel() {
        viewModel.budgetListLD.observe(viewLifecycleOwner) { budgets ->
            this.budgetList = budgets
            val budgetNames = budgets.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, budgetNames)
            binding.autoCompleteBudget.setAdapter(adapter)
            binding.autoCompleteBudget.setOnItemClickListener { _, _, position, _ ->
                selectedBudget = budgets[position]
                selectedBudget?.let {
                    viewModel.getTotalExpenseForBudget(it.id)
                }
            }
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { total ->
            currentTotalExpense = total ?: 0
            updateBudgetInfo()
        }

        updateBudgetInfo()
    }

    private fun updateBudgetInfo() {
        selectedBudget?.let { budget ->
            val budgetLeft = budget.nominal - currentTotalExpense

            binding.textBudgetUsed.text = "IDR %,d".format(currentTotalExpense)
            binding.textBudgetMax.text = "IDR %,d".format(budget.nominal)
            binding.textBudgetLeft.text = "(IDR %,d left)".format(budgetLeft)

            binding.progressBarBudget.progress = if (budget.nominal > 0) {
                (currentTotalExpense * 100) / budget.nominal
            } else {
                0
            }
        } ?: run {
            binding.textBudgetUsed.text = "IDR 0"
            binding.textBudgetMax.text = "IDR 0"
            binding.textBudgetLeft.text = ""
            binding.progressBarBudget.progress = 0
        }
    }

    private fun handleAddExpense() {
        val nominalString = binding.editNominal.text.toString()
        val notes = binding.editNotes.text.toString()

        if (selectedBudget == null) {
            Toast.makeText(context, "Please select a budget", Toast.LENGTH_SHORT).show()
            return
        }
        if (nominalString.isBlank()) {
            binding.textFieldNominal.error = "Nominal cannot be empty"
            return
        }
        if (notes.isBlank()) {
            binding.textFieldNotes.error = "Notes cannot be empty"
            return
        }

        val nominal = nominalString.toInt()
        val budgetLeft = selectedBudget!!.nominal - currentTotalExpense
        if (nominal <= 0) {
            binding.textFieldNominal.error = "Nominal must be positive"
            return
        }
        if (nominal > budgetLeft) {
            binding.textFieldNominal.error = "Nominal exceeds remaining budget"
            return
        }

        binding.textFieldNominal.error = null
        binding.textFieldNotes.error = null

        val newExpense = Expense(
            budgetId = selectedBudget!!.id,
            nominal = nominal,
            notes = notes,
            date = System.currentTimeMillis()
        )

        viewModel.addExpense(newExpense)
        Toast.makeText(context, "Expense added successfully!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}