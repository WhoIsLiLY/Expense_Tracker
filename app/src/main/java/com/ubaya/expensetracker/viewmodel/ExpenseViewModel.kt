package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.BudgetDao
import com.ubaya.expensetracker.model.ExpenseTrackerDatabase
import com.ubaya.expensetracker.model.Expense
import com.ubaya.expensetracker.model.ExpenseDao
import com.ubaya.expensetracker.model.ExpenseWithBudget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class ExpenseViewModel(application: Application) :
    AndroidViewModel(application),
    CoroutineScope {

    val expenseLD = MutableLiveData<List<ExpenseWithBudget>>()
    val expenseLoadErrorLD = MutableLiveData<Boolean>()
    val expenseloadingLD = MutableLiveData<Boolean>()

    private val expenseDao: ExpenseDao

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        val db = ExpenseTrackerDatabase.getDatabase(application)
        expenseDao = db.expenseDao()
    }

    //Refresh or populate data to budgetLD from database
    fun refresh(userId: Int) {
        expenseloadingLD.value = true
        expenseLoadErrorLD.value = false
        launch {
//            val db = buildDb(getApplication())
            try {
                val expenses = withContext(Dispatchers.IO) {
                    expenseDao.getAllExpensesForUser(userId)
                }
                expenseLD.postValue(expenses)
            } catch (e: Exception) {
                expenseLoadErrorLD.postValue(true)
            } finally {
                expenseloadingLD.postValue(false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel() // Membatalkan semua coroutine yang sedang berjalan
    }

}
