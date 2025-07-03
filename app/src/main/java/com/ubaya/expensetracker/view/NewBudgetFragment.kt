package com.ubaya.expensetracker.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.ubaya.expensetracker.databinding.FragmentNewBudgetBinding
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.viewmodel.DetailBudgetViewModel

class NewBudgetFragment : Fragment() {
    private lateinit var binding: FragmentNewBudgetBinding
    private lateinit var viewModel: DetailBudgetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewBudgetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailBudgetViewModel::class.java)
        binding.btnAdd.setOnClickListener {
            val name = binding.txtName.text.toString()
            val nominal = binding.txtNominal.text.toString().toIntOrNull()

            var isValid = true
            binding.textInputLayoutName.error = null
            binding.textInputLayoutNominal.error = null

            if (name.isBlank()) {
                binding.textInputLayoutName.error = "Nama tidak boleh kosong"
                isValid = false
            }

            if (nominal == null) {
                binding.textInputLayoutNominal.error = "Nominal tidak boleh kosong"
                isValid = false
            } else if (nominal < 0) {
                binding.textInputLayoutNominal.error = "Nominal tidak boleh negatif"
                isValid = false
            }

            if (isValid && nominal != null) {
                val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
                val loggedInUserId = sharedPref.getInt("LOGGED_IN_USER_ID", -1) // -1 adalah nilai default jika tidak ditemukan

                if (loggedInUserId == -1) {
                    Toast.makeText(context, "Error: User session not found. Please re-login.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener // Keluar dari fungsi jika tidak ada user yang login
                }

                val budget = Budget(userId = loggedInUserId, name = name, nominal = nominal)
                viewModel.addBudget(budget)
                Toast.makeText(view.context, "Data added", Toast.LENGTH_LONG).show()
                Navigation.findNavController(it).popBackStack()
            }
        }
    }
}