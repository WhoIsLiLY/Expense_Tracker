package com.ubaya.expensetracker.view

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

/**
 * A simple [Fragment] subclass.
 * Use the [EditBudgetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditBudgetFragment : Fragment() {
    private lateinit var binding: FragmentEditBudgetBinding
    private lateinit var viewModel: DetailBudgetViewModel

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

        val id = EditBudgetFragmentArgs.fromBundle(requireArguments()).id
        viewModel.fetchBudget(id)

        binding.btnAdd.setOnClickListener {
            viewModel.updateBudget(id, binding.txtName.text.toString(), binding.txtNominal.text.toString().toInt())
            Toast.makeText(view.context, "Budget updated", Toast.LENGTH_SHORT).show()
            Navigation.findNavController(it).popBackStack()
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.budgetLD.observe(viewLifecycleOwner, Observer {
            binding.txtName.setText(it.name)
            binding.txtNominal.setText(it.nominal.toString())
        })
    }
}
