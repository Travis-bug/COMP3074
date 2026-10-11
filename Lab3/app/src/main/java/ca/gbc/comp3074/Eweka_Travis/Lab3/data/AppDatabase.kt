package ca.gbc.comp3074.Eweka_Travis.Lab3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room database. It manages one entity (Customer) and stores it in SQLite.
 */
@Database(
    entities = [Customer::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton: only one database instance is created for the whole app
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "customer_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
