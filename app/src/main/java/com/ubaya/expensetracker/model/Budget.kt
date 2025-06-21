package com.ubaya.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    // Mendefinisikan Foreign Key
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"], // <-- DIUBAH: Sekarang menunjuk ke 'id'
        childColumns = ["user_id"], // <-- DIUBAH: Kolom baru untuk menampung id user
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["user_id"])] // <-- DIUBAH
)
data class Budget (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id: Int = 0,
    @ColumnInfo(name="name")
    var name: String,
    @ColumnInfo(name = "user_id")
    var userId: Int,
    @ColumnInfo(name="nominal")
    var nominal: Int,
)