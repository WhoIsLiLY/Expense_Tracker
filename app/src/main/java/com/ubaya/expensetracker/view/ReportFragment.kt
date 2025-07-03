package com.ubaya.expensetracker.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubaya.expensetracker.R
import com.ubaya.expensetracker.databinding.FragmentExpenseTrackerBinding
import com.ubaya.expensetracker.databinding.FragmentReportBinding
import com.ubaya.expensetracker.viewmodel.DetailExpenseViewModel
import com.ubaya.expensetracker.viewmodel.ExpenseViewModel
import java.text.NumberFormat
import java.util.Locale

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private lateinit var viewModel: DetailExpenseViewModel
    private val reportAdapter = ListReportAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DetailExpenseViewModel::class.java)
        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPref.getInt("LOGGED_IN_USER_ID", -1)

        if (loggedInUserId != -1) {
            viewModel.refreshReport(loggedInUserId)
        }
        // Setup RecyclerView
        binding.recyclerViewReport.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }

        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.reportDataLD.observe(viewLifecycleOwner, Observer {
            reportAdapter.updateReportList(it)
            if(it.isEmpty()) {
                binding.recyclerViewReport?.visibility = View.GONE
                binding.txtError2.text = "No records"
            } else {
                binding.recyclerViewReport?.visibility = View.VISIBLE
                val totalExpenses = it.sumOf { it.totalExpense ?: 0 }
                val totalBudget = it.sumOf { it.budgetNominal }

                val nf = NumberFormat.getNumberInstance(Locale("in", "ID"))
                val formattedExpenses = nf.format(totalExpenses)
                val formattedBudget = nf.format(totalBudget)

                binding.textTotalReport.text = "IDR $formattedExpenses / IDR $formattedBudget"
            }
        })

        viewModel.loadingLD.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                binding.progressLoad2?.visibility = View.GONE
            } else {
                binding.progressLoad2?.visibility = View.VISIBLE
            }
        })

        viewModel.loadErrorLD.observe(viewLifecycleOwner, Observer {
            if(it == false) {
                binding.txtError2?.visibility = View.GONE
            } else {
                binding.txtError2?.visibility = View.VISIBLE
            }
        })
    }
}