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
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class ListBudgetViewModel(application: Application) :
    AndroidViewModel(application),
    CoroutineScope {

    val budgetLD = MutableLiveData<List<Budget>>()
    val budgetLoadErrorLD = MutableLiveData<Boolean>()
    val budgetloadingLD = MutableLiveData<Boolean>()

    private val budgetDao: BudgetDao

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    init {
        val db = BudgetDatabase.getDatabase(application)
        budgetDao = db.budgetDao()
    }

    //Refresh or populate data to budgetLD from database
    fun refresh(userId: Int) {
        budgetloadingLD.value = true
        budgetLoadErrorLD.value = false
        launch {
//            val db = buildDb(getApplication())
            try {
                val budgets = withContext(Dispatchers.IO) {
                    budgetDao.getAllBudgetsForUser(userId)
                }
                budgetLD.postValue(budgets)
            } catch (e: Exception) {
                budgetLoadErrorLD.postValue(true)
            } finally {
                budgetloadingLD.postValue(false)
            }
        }
    }

    fun deleteBudget(budget: Budget, userId: Int) {
        launch {
            try {
                withContext(Dispatchers.IO) {
                    budgetDao.deleteBudget(budget)
                }
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