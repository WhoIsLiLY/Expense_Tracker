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
    /**
     * Menyisipkan satu budget baru ke dalam database.
     * Jika budget dengan ID yang sama sudah ada, maka akan diganti.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBudget(budget: Budget)

    /**
     * Mengambil semua budget HANYA untuk user yang sedang login.
     * @param userId ID dari user yang sedang login.
     * @return Daftar budget milik user tersebut.
     */
    @Query("SELECT * FROM budget WHERE user_id = :userId ORDER BY id DESC")
    fun getAllBudgetsForUser(userId: Int): List<Budget>

    /**
     * Mengambil satu budget spesifik, dengan memastikan budget tersebut milik user yang benar.
     * @param id ID dari budget yang dicari.
     * @param userId ID dari user yang sedang login.
     * @return Objek Budget jika ditemukan dan cocok dengan user, atau null jika tidak.
     */
    @Query("SELECT * FROM budget WHERE id = :id AND user_id = :userId")
    fun getBudgetById(id: Int, userId: Int): Budget?

    /**
     * Memperbarui data budget, dengan memastikan hanya pemilik yang bisa mengubahnya.
     * @param id ID dari budget yang akan diubah.
     * @param userId ID dari user yang sedang login (untuk keamanan).
     * @param name Nama baru untuk budget.
     * @param nominal Nominal baru untuk budget.
     */
    @Query("UPDATE budget SET name = :name, nominal = :nominal WHERE id = :id AND user_id = :userId")
    fun updateBudget(id: Int, userId: Int, name: String, nominal: Int)

    /**
     * Menghapus sebuah budget dari database.
     * Anotasi @Delete bekerja dengan mencocokkan Primary Key dari objek yang diberikan.
     */
    @Delete
    fun deleteBudget(budget: Budget)
}