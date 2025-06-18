package com.ubaya.expensetracker.model

import DB_NAME
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Database(entities = arrayOf(Budget::class), version =  2)
abstract class BudgetDatabase: RoomDatabase() {
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile private var instance: BudgetDatabase ?= null
        private val LOCK = Any()

//        private val roomCallback = object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                // Prepopulate with data
//                CoroutineScope(Dispatchers.IO).launch {
//                    instance?.budgetDao()?.insertAllBudget(
//                        Budget(
//                            name = "Healing",
//                            nominal = 10000000
//                        )
//                    )
//                    instance?.budgetDao()?.insertAllBudget(
//                        Budget(
//                            name = "Makan",
//                            nominal = 1000000
//                        )
//                    )
//                }
//            }
//        }

//        fun getDatabase(context: Context): BudgetDatabase {
//            return instance ?: synchronized(this) {
//                val newInstance = Room.databaseBuilder(
//                    context.applicationContext,
//                    BudgetDatabase::class.java, DB_NAME)
//                    .addCallback(roomCallback)
//                    .build()
//                instance = newInstance
//                newInstance
//            }
//        }

        fun getDatabase(context: Context, onPrepopulateFinished: () -> Unit = {}): BudgetDatabase {
            val roomCallback = object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        instance?.budgetDao()?.insertAllBudget(
                            Budget(name = "Healing", nominal = 10_000_000)
                        )
                        instance?.budgetDao()?.insertAllBudget(
                            Budget(name = "Makan", nominal = 1_000_000)
                        )

                        // Panggil callback setelah selesai prepopulate
                        onPrepopulateFinished()
                    }
                }
            }

            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java, DB_NAME
                )
                    .addCallback(roomCallback)
                    .build()
                instance = newInstance
                newInstance
            }
        }

//        fun buildDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                BudgetDatabase::class.java, DB_NAME)
//                .build()
//
//        operator fun invoke(context: Context) {
//            if(instance == null) {
//                synchronized(LOCK) {
//                    instance ?: buildDatabase(context).also {
//                        instance = it
//                    }
//                }
//            }
//        }
    }
}