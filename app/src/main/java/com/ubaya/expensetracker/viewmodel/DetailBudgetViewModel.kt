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
import kotlin.coroutines.CoroutineContext

class DetailBudgetViewModel(private val budgetDao: BudgetDao) : ViewModel(), CoroutineScope {

    val budgetLD = MutableLiveData<Budget?>()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    fun addBudget(budget: Budget) {
        launch {
//            val db = buildDb(getApplication())
            budgetDao.insertBudget(budget)
        }
    }

    fun fetchBudgetById(budgetId: Int, userId: Int) {
        // Coroutine tetap dimulai di context default (IO)
        launch {
            // 1. Ambil data di background thread (ini aman dan efisien)
            val budget = budgetDao.getBudgetById(budgetId, userId)

            // 2. Pindah ke Main thread secara eksplisit untuk update LiveData
            withContext(Dispatchers.Main) {
                // 3. Gunakan .value karena kita sudah pasti di Main Thread
                budgetLD.value = budget
            }
        }
    }

    fun updateBudget(budgetId: Int, userId: Int, name: String, nominal: Int) {
        launch {
            budgetDao.updateBudget(budgetId, userId, name, nominal)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel() // Membatalkan semua coroutine yang sedang berjalan di dalam scope ini
    }
}
