package com.ubaya.expensetracker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.ubaya.expensetracker.R
import com.ubaya.expensetracker.databinding.FragmentNewBudgetBinding
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.viewmodel.DetailBudgetViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [NewBudget.newInstance] factory method to
 * create an instance of this fragment.
 */
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
                val budget = Budget(name = name, nominal = nominal)
                val list = listOf(budget)
                viewModel.addBudget(list)
                Toast.makeText(view.context, "Data added", Toast.LENGTH_LONG).show()
                Navigation.findNavController(it).popBackStack()
            }
        }
    }
}