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
import kotlin.coroutines.CoroutineContext

class DetailBudgetViewModel(application: Application)
    : AndroidViewModel(application), CoroutineScope {

    val budgetLD = MutableLiveData<Budget>()
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    fun addBudget(list: List<Budget>) {
        launch {
            val db = buildDb(getApplication())
            db.budgetDao().insertAllBudget(*list.toTypedArray())
        }
    }

    fun fetchBudget(id:Int) {
        launch {
            val db = buildDb(getApplication())
            budgetLD.postValue(db.budgetDao().selectBudget(id))
        }
    }

    fun updateBudget(id:Int, name:String, nominal:Int) {
        launch {
            val db = buildDb(getApplication())
            db.budgetDao().updateBudget(id, name, nominal)
        }
    }
}
