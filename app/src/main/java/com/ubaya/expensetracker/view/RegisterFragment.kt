package com.ubaya.expensetracker.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ubaya.expensetracker.databinding.FragmentRegisterBinding // Import ViewBinding
import com.ubaya.expensetracker.model.User
import com.ubaya.expensetracker.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setup ViewBinding
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Mengamati status registrasi dari ViewModel
        viewModel.registrationStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                // Kembali ke halaman login setelah berhasil
                findNavController().navigate(RegisterFragmentDirections.actionRegisterToLogin())
            } else {
                Toast.makeText(context, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show()
            }
        }

        // Listener untuk tombol kembali
        binding.imageViewBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Listener untuk tombol Create Account
        binding.buttonCreateAccount.setOnClickListener {
            handleRegistration()
        }
    }

    private fun handleRegistration() {
        val username = binding.editTextUsername.text.toString()
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val password = binding.editTextPassword.text.toString()
        val repeatPassword = binding.editTextRepeatPassword.text.toString()

        // 1. Validasi input tidak boleh kosong
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "All fields must be filled", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validasi password harus sama
        if (password != repeatPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Buat objek User dengan password yang sudah di-hash
        val hashedPassword = viewModel.hashString(password)
        val newUser = User(
            username = username,
            firstName = firstName,
            lastName = lastName,
            password = hashedPassword // Simpan password yang sudah di-hash
        )

        // 4. Panggil fungsi register di ViewModel
        viewModel.registerUser(newUser)
    }
}