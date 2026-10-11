package ca.gbc.comp3074.Eweka_Travis.Lab3.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the single "customers" table.
 * Each Customer object is one row in the table.
 */
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val age: Int,
    val isActive: Boolean
)
