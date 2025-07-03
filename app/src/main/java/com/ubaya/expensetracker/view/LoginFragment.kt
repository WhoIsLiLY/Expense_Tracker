package com.ubaya.expensetracker.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ubaya.expensetracker.databinding.FragmentLoginBinding // Import ViewBinding
import com.ubaya.expensetracker.model.User
import com.ubaya.expensetracker.viewmodel.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Mengamati hasil login dari ViewModel
        viewModel.loginResult.observe(viewLifecycleOwner) { user ->
            // Tambahkan pengecekan untuk menghindari trigger saat fragment pertama kali dibuat
            if (user == null && (binding.editTextUsernameLogin.text?.isNotEmpty() ?: false)) {
                Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                // Jika user tidak null, berarti login berhasil
                onLoginSuccess(user)
            }
        }

        // Listener untuk tombol Sign In
        binding.buttonSignIn.setOnClickListener {
            handleLogin()
        }

        // Listener untuk tombol Sign Up (untuk pindah ke halaman registrasi)
        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginToRegister())
        }
    }

    private fun handleLogin() {
        val username = binding.editTextUsernameLogin.text.toString()
        val password = binding.editTextPasswordLogin.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            viewModel.login(username, password)
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onLoginSuccess(user: User) {
        // Tampilkan pesan sukses
        Toast.makeText(context, "Welcome, ${user.firstName}!", Toast.LENGTH_SHORT).show()

        // Simpan sesi login menggunakan SharedPreferences
        val sharedPref = activity?.getSharedPreferences("ExpenseTrackerPrefs", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("IS_LOGGED_IN", true)
            putInt("LOGGED_IN_USER_ID", user.id) // Simpan ID user, ini SANGAT PENTING
            putString("LOGGED_IN_USERNAME", user.username)
            apply()
        }

        // Pindah ke MainActivity
        val intent = Intent(activity, MainActivity::class.java)
        // Flag ini akan membersihkan back stack, sehingga user tidak bisa kembali ke halaman login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Tutup AuthActivity
    }
}