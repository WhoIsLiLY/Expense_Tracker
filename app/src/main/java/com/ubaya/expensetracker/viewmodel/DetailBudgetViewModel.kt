package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import buildDb
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.BudgetDao
import com.ubaya.expensetracker.model.BudgetDatabase
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
    private val job = Job()

    private val budgetDao: BudgetDao

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        val db = BudgetDatabase.getDatabase(application)
        budgetDao = db.budgetDao()
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
