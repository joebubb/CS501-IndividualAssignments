package com.josephbubb.todo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks") // Define the table name
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated ID
    val description: String,                        // Text of the task
    var isCompleted: Boolean = false                // Completion status
)