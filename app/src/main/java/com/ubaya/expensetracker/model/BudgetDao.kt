package com.ubaya.expensetracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BudgetDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAllBudget(vararg budget: Budget)
//
//    @Query("SELECT * FROM budget")
//    fun selectAllBudget(): List<Budget>

//    @Query("SELECT * FROM budget WHERE user_username = :username ORDER BY id DESC")
//    suspend fun selectAllBudget(username: String): List<Budget>
//
//    @Query("SELECT * FROM budget WHERE id= :id")
//    fun selectBudget(id:Int): Budget
//
//    @Query("UPDATE budget SET name=:name, nominal=:nominal WHERE id = :id")
//    fun updateBudget(id:Int, name:String, nominal:Int)
//
//    @Delete
//    fun deleteBudget(student: Budget)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBudget(budget: Budget)

    @Query("SELECT * FROM budget WHERE user_id = :userId ORDER BY id DESC")
    fun getAllBudgetsForUser(userId: Int): List<Budget>

    @Query("SELECT * FROM budget WHERE id = :id AND user_id = :userId")
    fun getBudgetById(id: Int, userId: Int): Budget?

    @Query("UPDATE budget SET name = :name, nominal = :nominal WHERE id = :id AND user_id = :userId")
    fun updateBudget(id: Int, userId: Int, name: String, nominal: Int)

    @Delete
    fun deleteBudget(budget: Budget)
}