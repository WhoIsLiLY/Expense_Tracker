package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import buildDb
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.BudgetDao
import com.ubaya.expensetracker.model.BudgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class ListBudgetViewModel(private val budgetDao: BudgetDao) : ViewModel(), CoroutineScope {

    val budgetLD = MutableLiveData<List<Budget>>()
    val budgetLoadErrorLD = MutableLiveData<Boolean>()
    val budgetloadingLD = MutableLiveData<Boolean>()
    private var job = Job()

    val needRefresh = MutableLiveData<Boolean>()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    //Refresh or populate data to budgetLD from database
    fun refresh(userId: Int) {
        budgetloadingLD.value = true
        budgetLoadErrorLD.value = false
        launch {
//            val db = buildDb(getApplication())
            try {
                val budgets = budgetDao.getAllBudgetsForUser(userId)
                budgetLD.postValue(budgets)
            } catch (e: Exception) {
                budgetLoadErrorLD.postValue(true)
            } finally {
                budgetloadingLD.postValue(false)
            }
        }
    }

    //Delete budget (Seharusnya tidak perlu)
    fun deleteBudget(budget: Budget, userId: Int) {
        launch {
            try {
                budgetDao.deleteBudget(budget)
                refresh(userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel() // Membatalkan semua coroutine yang sedang berjalan
    }
}