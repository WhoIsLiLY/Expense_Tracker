package com.ubaya.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import buildDb
import com.ubaya.expensetracker.model.Budget
import com.ubaya.expensetracker.model.BudgetDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class ListBudgetViewModel(application: Application)
    : AndroidViewModel(application), CoroutineScope {

    val budgetLD = MutableLiveData<List<Budget>>()
    val budgetLoadErrorLD = MutableLiveData<Boolean>()
    val budgetloadingLD = MutableLiveData<Boolean>()
    private var job = Job()

    val needRefresh = MutableLiveData<Boolean>()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    //Refresh or populate data to budgetLD from database
    fun refresh() {
        budgetloadingLD.value = true
        budgetLoadErrorLD.value = false
        launch {
//            val db = buildDb(getApplication())
            val db = BudgetDatabase.getDatabase(getApplication()) {
                needRefresh.postValue(true)
            }

            budgetLD.postValue(db.budgetDao().selectAllBudget())
            budgetloadingLD.postValue(false)
        }
    }

    //Delete budget (Seharusnya tidak perlu)
    fun clearBudget(budget: Budget) {
        launch {
            val db = buildDb(getApplication())
            db.budgetDao().deleteBudget(budget)

            budgetLD.postValue(db.budgetDao().selectAllBudget())
        }
    }
}