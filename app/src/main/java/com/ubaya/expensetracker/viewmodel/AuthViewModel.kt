package com.ubaya.expensetracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ubaya.expensetracker.model.ExpenseTrackerDatabase
import com.ubaya.expensetracker.model.User
import com.ubaya.expensetracker.model.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import kotlin.coroutines.CoroutineContext

class AuthViewModel(application: Application) :
    AndroidViewModel(application),
    CoroutineScope {

    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> = _loginResult

    private val userDao: UserDao

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        val db = ExpenseTrackerDatabase.getDatabase(application)
        userDao = db.userDao()
    }

    fun registerUser(user: User) {
        launch {
            try {
                withContext(Dispatchers.IO) {
                    userDao.insertUser(user)
                }
                _registrationStatus.postValue(true)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration database error", e)
                _registrationStatus.postValue(false)
            }
        }
    }

    fun login(username: String, password_plain: String) {
        launch {
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}