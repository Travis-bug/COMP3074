package ca.gbc.comp3074.Eweka_Travis.Lab3.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object): the database operations the rest of the app is allowed to use.
 */
@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: Customer)

    @Delete
    suspend fun delete(customer: Customer)

    // Returning Flow means Room sends a new list every time the table changes
    @Query("SELECT * FROM customers ORDER BY id")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query(
        "SELECT * FROM customers " +
            "WHERE name LIKE '%' || :searchText || '%' " +
            "ORDER BY name"
    )
    fun searchCustomers(searchText: String): Flow<List<Customer>>
}
