package com.ubaya.expensetracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBudget(vararg budget: Budget)

    @Query("SELECT * FROM budget")
    fun selectAllBudget(): List<Budget>

    @Query("SELECT * FROM budget WHERE id= :id")
    fun selectBudget(id:Int): Budget

    @Query("UPDATE budget SET name=:name, nominal=:nominal WHERE id = :id")
    fun updateBudget(id:Int, name:String, nominal:Int)

    @Delete
    fun deleteBudget(student: Budget)
}