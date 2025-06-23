package com.ubaya.expensetracker.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // onConflict ABORT akan menyebabkan error jika username sudah ada, bagus untuk registrasi
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    fun selectUser(username: String): User?

    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    fun selectUserById(userId: Int): User?

    @Update
    fun updateUser(user: User)
}