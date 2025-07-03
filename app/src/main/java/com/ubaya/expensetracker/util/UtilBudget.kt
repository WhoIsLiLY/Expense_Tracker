import android.content.Context
import androidx.room.Room
import com.ubaya.expensetracker.model.ExpenseTrackerDatabase

val DB_NAME = "newbudgetdb"

fun buildDb(context: Context): ExpenseTrackerDatabase {
    val db = Room.databaseBuilder(context,
        ExpenseTrackerDatabase::class.java, DB_NAME).build()
    return db
}
