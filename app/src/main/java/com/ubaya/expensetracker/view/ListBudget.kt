package com.ubaya.expensetracker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubaya.expensetracker.R
import com.ubaya.expensetracker.databinding.FragmentListBudgetBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ListBudget.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListBudget : Fragment() {
    private lateinit var binding: FragmentListBudgetBinding

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

//        viewModel = ViewModelProvider(this).get(ListTodoViewModel::class.java)
//        viewModel.refresh()
//        binding.recViewTodo.layoutManager = LinearLayoutManager(context)
//        binding.recViewTodo.adapter = todoListAdapter

//        binding.btnFab.setOnClickListener {
//            val action = TodoListFragmentDirections.actionCreateTodo()
//            Navigation.findNavController(it).navigate(action)
//        }
//
//        observeViewModel()
    }
}