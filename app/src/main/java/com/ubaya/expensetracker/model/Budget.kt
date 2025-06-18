package com.ubaya.expensetracker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Budget (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id: Int = 0,
    @ColumnInfo(name="name")
    var name: String,
    @ColumnInfo(name="nominal")
    var nominal: Int,
)