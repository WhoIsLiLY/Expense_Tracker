package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.BudgetDao
import com.ubaya.expensetracker.model.ExpenseDao
import com.ubaya.expensetracker.model.ExpenseTrackerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DetailBudgetViewModel(application: Application) :
    AndroidViewModel(application)
    , CoroutineScope {

    val budgetLD = MutableLiveData<Budget?>()
    val totalExpense = MutableLiveData<Int>()
    private val job = Job()

    private val budgetDao: BudgetDao
    private val expenseDao: ExpenseDao

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        val db = ExpenseTrackerDatabase.getDatabase(application)
        budgetDao = db.budgetDao()
        expenseDao = db.expenseDao()
    }

    fun addBudget(budget: Budget) {
        launch {
            withContext(Dispatchers.IO) {
                budgetDao.insertBudget(budget)
            }
        }
    }

    fun fetchBudgetById(budgetId: Int, userId: Int) {
        launch {
            val budget = budgetDao.getBudgetById(budgetId, userId)
            // postValue lebih aman di sini jika ada kemungkinan dipanggil dari background
            budgetLD.postValue(budget)
        }
    }

    fun updateBudget(budgetId: Int, userId: Int, name: String, nominal: Int) {
        launch {
            budgetDao.updateBudget(budgetId, userId, name, nominal)
        }
    }

    fun getTotalExpenseForBudget(budgetId: Int) {
        viewModelScope.launch {
            try {
                val totalExpenseResponse = expenseDao.getTotalExpenseForBudget(budgetId)
                totalExpense.postValue(totalExpenseResponse)
            } catch (e: Exception) {

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
