package com.ubaya.expensetracker.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.ubaya.expensetracker.databinding.FragmentEditBudgetBinding
import com.ubaya.expensetracker.viewmodel.DetailBudgetViewModel

class EditBudgetFragment : Fragment() {
    private lateinit var binding: FragmentEditBudgetBinding
    private lateinit var viewModel: DetailBudgetViewModel
    private var budgetId = 0

    private var currentTotalExpense: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DetailBudgetViewModel::class.java)
//        val id = EditBudgetFragmentArgs.fromBundle(requireArguments()).id
        arguments?.let {
            budgetId = EditBudgetFragmentArgs.fromBundle(it).id
        }
        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPref.getInt("LOGGED_IN_USER_ID", -1)

        // Pastikan userId valid sebelum melakukan fetch
        if (loggedInUserId != -1 && budgetId != 0) {
            // 2. Panggil fungsi fetch yang benar dengan menyertakan userId
            viewModel.fetchBudgetById(budgetId, loggedInUserId)
            viewModel.getTotalExpenseForBudget(budgetId)
        }
//        viewModel.fetchBudget(id)

        binding.btnAdd.setOnClickListener {
            // Lakukan validasi dan update
            val name = binding.txtName.text.toString()
            // 4. Gunakan toIntOrNull() yang lebih aman
            val nominal = binding.txtNominal.text.toString().toIntOrNull()

            if (name.isNotEmpty() && nominal != null && nominal > 0 && loggedInUserId != -1) {
                // 3. Panggil fungsi update yang benar dengan menyertakan userId
                if (nominal<currentTotalExpense) {
                    Toast.makeText(view.context, "Budget minimum is "+currentTotalExpense.toString(), Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.updateBudget(budgetId, loggedInUserId, name, nominal)
                    Toast.makeText(view.context, "Budget updated", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(it).popBackStack()
                }
            } else {
                Toast.makeText(view.context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.budgetLD.observe(viewLifecycleOwner, Observer { budget ->
            // 'it' di sini adalah 'budget'
            if (budget != null) {
                // Hanya set text jika budget tidak null
                binding.txtName.setText(budget.name)
                binding.txtNominal.setText(budget.nominal.toString())
            } else {
                Toast.makeText(context, "Budget not found or error loading data.", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.totalExpense.observe(viewLifecycleOwner) { total ->
            currentTotalExpense = total ?: 0
        }
    }
}
