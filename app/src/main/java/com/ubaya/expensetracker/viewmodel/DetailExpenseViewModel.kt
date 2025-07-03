package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.BudgetDao
import com.ubaya.expensetracker.model.Expense
import com.ubaya.expensetracker.model.ExpenseDao
import com.ubaya.expensetracker.model.ExpenseTrackerDatabase
import com.ubaya.expensetracker.model.ReportData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class DetailExpenseViewModel(application: Application) :
    AndroidViewModel(application),
    CoroutineScope {

    val budgetListLD = MutableLiveData<List<Budget>>()
    val reportDataLD = MutableLiveData<List<ReportData>>()
    val loadingLD = MutableLiveData<Boolean>()
    val loadErrorLD = MutableLiveData<Boolean>()
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

    fun addExpense(expense: Expense) {
        launch {
            withContext(Dispatchers.IO) {
                expenseDao.insertExpense(expense)
            }
        }
    }

    fun refreshBudgets(userId: Int) {
        launch {
            try {
                val budgets = budgetDao.getAllBudgetsForUser(userId)
                budgetListLD.postValue(budgets)
            } catch (e: Exception) {
                // Handle error jika diperlukan
            }
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

//    // Fungsi untuk mengambil data laporan
    fun refreshReport(userId: Int) {
        loadingLD.value = true
        loadErrorLD.value = false
        launch {
    //            val db = buildDb(getApplication())
            try {
                val reportData = withContext(Dispatchers.IO) {
                    expenseDao.getReportData(userId)
                }
                reportDataLD.postValue(reportData)
            } catch (e: Exception) {
                loadErrorLD.postValue(true)
            } finally {
                loadingLD.postValue(false)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        job.cancel() // Membatalkan semua coroutine yang sedang berjalan
    }

}