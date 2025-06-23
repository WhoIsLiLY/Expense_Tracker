package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubaya.expensetracker.model.BudgetDatabase
import com.ubaya.expensetracker.model.User
import com.ubaya.expensetracker.model.UserDao
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val userDao: UserDao

    init {
        // Gunakan 'application' yang didapat dari konstruktor untuk membangun database
        val db = BudgetDatabase.getDatabase(application)
        userDao = db.userDao()
    }

    fun registerUser(user: User) {
        viewModelScope.launch {
            try {
                userDao.insertUser(user)
                _registrationStatus.postValue(true)
            } catch (e: Exception) {
                // Terjadi error, kemungkinan karena username sudah ada (OnConflictStrategy.ABORT)
                android.util.Log.e("AuthViewModel", "Registration database error", e)
                _registrationStatus.postValue(false)
            }
        }
    }

    fun login(username: String, password_plain: String) {
        viewModelScope.launch {
            val user = userDao.selectUser(username)
            if (user != null && user.password == hashString(password_plain)) {
                _loginResult.postValue(user)
            } else {
                _loginResult.postValue(null)
            }
        }
    }

    // Utility untuk hash password
    fun hashString(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}