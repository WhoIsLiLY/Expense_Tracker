package com.ubaya.expensetracker.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubaya.expensetracker.databinding.FragmentListBudgetBinding
import com.ubaya.expensetracker.model.BudgetDatabase
import com.ubaya.expensetracker.viewmodel.ListBudgetViewModel
import com.ubaya.expensetracker.viewmodel.ListBudgetViewModelFactory

/**
 * A simple [Fragment] subclass.
 * Use the [ListBudgetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListBudgetFragment : Fragment() {
    private lateinit var binding: FragmentListBudgetBinding
    private lateinit var viewModel: ListBudgetViewModel
    private val budgetListAdapter = ListBudgetAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBudgetBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val application = requireNotNull(this.activity).application
        val budgetDao = BudgetDatabase.getDatabase(application).budgetDao()

        val viewModelFactory = ListBudgetViewModelFactory(budgetDao)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ListBudgetViewModel::class.java)

        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPref.getInt("LOGGED_IN_USER_ID", -1)

        if (loggedInUserId != -1) {
            viewModel.refresh(loggedInUserId)
        }

        binding.recViewBudget.layoutManager = LinearLayoutManager(context)
        binding.recViewBudget.adapter = budgetListAdapter

        binding.btnFab.setOnClickListener {
            val action = ListBudgetFragmentDirections.actionNewBudget()
            Navigation.findNavController(it).navigate(action)
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.budgetLD.observe(viewLifecycleOwner, Observer {
            budgetListAdapter.updateBudgetList(it)
            if(it.isEmpty()) {
                binding.recViewBudget?.visibility = View.GONE
                binding.txtError.setText("Your todo still empty.")
            } else {
                binding.recViewBudget?.visibility = View.VISIBLE
            }
        })

        viewModel.budgetloadingLD.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                binding.progressLoad?.visibility = View.GONE
            } else {
                binding.progressLoad?.visibility = View.VISIBLE
            }
        })

        viewModel.budgetLoadErrorLD.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                binding.txtError?.visibility = View.GONE
            } else {
                binding.txtError?.visibility = View.VISIBLE
            }
        })
    }
}