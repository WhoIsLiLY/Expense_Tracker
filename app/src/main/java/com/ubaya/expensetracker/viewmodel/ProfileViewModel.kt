package com.ubaya.expensetracker.viewmodel

import android.app.Application // Import Application
import androidx.lifecycle.AndroidViewModel // Import AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ubaya.expensetracker.model.ExpenseTrackerDatabase
import com.ubaya.expensetracker.model.UserDao
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _passwordChangeStatus = MutableLiveData<Boolean>()
    val passwordChangeStatus: LiveData<Boolean> = _passwordChangeStatus

    private val _changePasswordEvent = MutableLiveData<Boolean?>()
    val changePasswordEvent: LiveData<Boolean?> = _changePasswordEvent

    private val _signOutEvent = MutableLiveData<Boolean?>()
    val signOutEvent: LiveData<Boolean?> = _signOutEvent

    private val userDao: UserDao

    init {
        val db = ExpenseTrackerDatabase.getDatabase(application)
        userDao = db.userDao()
    }

    fun changePassword(userId: Int, oldPass: String, newPass: String) {
        viewModelScope.launch {
            val user = userDao.selectUserById(userId)
            if (user != null && user.password == hashString(oldPass)) {
                user.password = hashString(newPass)
                userDao.updateUser(user)
                _passwordChangeStatus.postValue(true)
            } else {
                _passwordChangeStatus.postValue(false)
            }
        }
    }

    fun onChangePasswordClick() {
        _changePasswordEvent.value = true
    }

    fun onSignOutClick() {
        _signOutEvent.value = true
    }

    fun doneNavigating() {
        _changePasswordEvent.value = null
        _signOutEvent.value = null
    }

    fun hashString(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}