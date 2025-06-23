package com.ubaya.expensetracker.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ubaya.expensetracker.R
import com.ubaya.expensetracker.databinding.FragmentProfileBinding
import com.ubaya.expensetracker.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.passwordChangeStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()

                binding.editTextOldPassword.text?.clear()
                binding.editTextNewPassword.text?.clear()
                binding.editTextRepeatPassword.text?.clear()
            } else {
                Toast.makeText(context, "Failed to change password. Old password might be incorrect.", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonChangePassword.setOnClickListener {
            handleChangePassword()
        }

        binding.buttonSignOut.setOnClickListener {
            handleSignOut()
        }
    }

    private fun handleChangePassword() {
        val oldPassword = binding.editTextOldPassword.text.toString()
        val newPassword = binding.editTextNewPassword.text.toString()
        val repeatPassword = binding.editTextRepeatPassword.text.toString()

        if (oldPassword.isEmpty() || newPassword.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(context, "All password fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != repeatPassword) {
            Toast.makeText(context, "New password and repeat password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("LOGGED_IN_USER_ID", -1)

        if (userId != -1) {
            viewModel.changePassword(userId, oldPassword, newPassword)
        } else {
            Toast.makeText(context, "User session not found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSignOut() {
        val sharedPref = requireActivity().getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("IS_LOGGED_IN")
            remove("LOGGED_IN_USER_ID")
            remove("LOGGED_IN_USERNAME")
            apply()
        }

        val intent = Intent(activity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}