package com.ubaya.expensetracker.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

data class ExpenseWithBudget(
    val expenseId: Int,
    val nominal: Int,
    val notes: String,
    val date: Long,
    val budgetName: String,
)

data class ReportData(
    val budgetId: Int,
    val budgetName: String,
    val budgetNominal: Int,
    val totalExpense: Int?
)

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpense(expense: Expense)

    @Query("SELECT e.id as expenseId, e.nominal, e.notes, e.date, b.name as budgetName FROM expenses e INNER JOIN budget b ON e.budget_id = b.id WHERE b.user_id = :userId ORDER BY e.date DESC")
    fun getAllExpensesForUser(userId: Int): List<ExpenseWithBudget>

    @Query("""SELECT 
            b.id as budgetId, 
            b.name as budgetName, 
            b.nominal as budgetNominal, 
            (SELECT SUM(e.nominal) FROM expenses e WHERE e.budget_id = b.id) as totalExpense 
        FROM budget b 
        WHERE b.user_id = :userId"""
    )
    fun getReportData(userId: Int): List<ReportData>

    @Query(
        """
            SELECT sum(e.nominal) as totalExpense from expenses e where e.budget_id = :budgetId
        """
    )
    fun getTotalExpenseForBudget(budgetId: Int): Int
}