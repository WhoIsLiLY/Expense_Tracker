package com.ubaya.expensetracker.model


import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest

@Database(entities = [Budget::class, User::class], version =  1)
abstract class ExpenseTrackerDatabase: RoomDatabase() {
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao

    companion object {
        private const val DB_NAME = "expensetracker.db"
        @Volatile private var instance: ExpenseTrackerDatabase ?= null
        private val LOCK = Any()

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    Log.d("PrepopulateDB", "Database pertama kali dibuat, mengisi data dummy...")

                    val userDao = instance?.userDao()
                    val budgetDao = instance?.budgetDao()

                    val dummyPassword = "123"
                    val hashedPassword = hashString(dummyPassword) // Hash passwordnya
                    val dummyUser = User(
                        username = "tests",
                        firstName = "Testing",
                        lastName = "Satu",
                        password = hashedPassword
                    )
                    userDao?.insertUser(dummyUser)

                    // B. Buat beberapa budget untuk user dengan id=1 (user pertama yang dibuat)
                    budgetDao?.insertBudget(Budget(userId = 1, name = "Makan Sebulan", nominal = 1500000))
                    budgetDao?.insertBudget(Budget(userId = 1, name = "Transportasi", nominal = 500000))
                    budgetDao?.insertBudget(Budget(userId = 1, name = "Hiburan & Healing", nominal = 750000))
                }
            }
        }

        private fun hashString(input: String): String {
            return MessageDigest.getInstance("SHA-256")
                .digest(input.toByteArray())
                .fold("") { str, it -> str + "%02x".format(it) }
        }

        fun getDatabase(context: Context): ExpenseTrackerDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ExpenseTrackerDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addCallback(roomCallback)
                .build()
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