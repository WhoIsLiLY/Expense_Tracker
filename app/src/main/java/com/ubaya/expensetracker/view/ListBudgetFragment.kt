package com.ubaya.expensetracker.view

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
import com.ubaya.expensetracker.viewmodel.ListBudgetViewModel

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

        viewModel = ViewModelProvider(this).get(ListBudgetViewModel::class.java)
        viewModel.refresh()
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

        viewModel.needRefresh.observe(viewLifecycleOwner) {
            if (it == true) {
                viewModel.refresh()
                viewModel.needRefresh.value = false
            }
        }
    }
}