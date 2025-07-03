package com.ubaya.expensetracker.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubaya.expensetracker.databinding.FragmentExpenseTrackerBinding
import com.ubaya.expensetracker.databinding.FragmentListBudgetBinding
import com.ubaya.expensetracker.viewmodel.ExpenseViewModel
import com.ubaya.expensetracker.viewmodel.ListBudgetViewModel

class ExpenseTrackerFragment : Fragment() {
    private lateinit var binding: FragmentExpenseTrackerBinding
    private lateinit var viewModel: ExpenseViewModel
    private val expenseAdapter = ListExpenseAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ExpenseViewModel::class.java)
        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPref.getInt("LOGGED_IN_USER_ID", -1)

        if (loggedInUserId != -1) {
            viewModel.refresh(loggedInUserId)
        }
        // Setup RecyclerView
        binding.recViewExpense.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }

        binding.btnFab.setOnClickListener {
            val action = ExpenseTrackerFragmentDirections.actionNewExpense()
            findNavController().navigate(action)
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.expenseLD.observe(viewLifecycleOwner, Observer {
            expenseAdapter.updateExpenseList(it)
            if(it.isEmpty()) {
                binding.recViewExpense?.visibility = View.GONE
                binding.txtError.setText("Your expense list are empty..")
            } else {
                binding.recViewExpense?.visibility = View.VISIBLE
            }
        })

        viewModel.expenseloadingLD.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                binding.progressLoad?.visibility = View.GONE
            } else {
                binding.progressLoad?.visibility = View.VISIBLE
            }
        })

        viewModel.expenseLoadErrorLD.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                binding.txtError?.visibility = View.GONE
            } else {
                binding.txtError?.visibility = View.VISIBLE
            }
        })
    }
}
