import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ubaya.expensetracker.model.BudgetDatabase

val DB_NAME = "newbudgetdb"

fun buildDb(context: Context): BudgetDatabase {
    val db = Room.databaseBuilder(context,
        BudgetDatabase::class.java, DB_NAME).build()
    return db
}
