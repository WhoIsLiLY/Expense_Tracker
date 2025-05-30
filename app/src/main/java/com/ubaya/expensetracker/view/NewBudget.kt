package com.ubaya.expensetracker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ubaya.expensetracker.R

/**
 * A simple [Fragment] subclass.
 * Use the [NewBudget.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewBudget : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_budget, container, false)
    }
}