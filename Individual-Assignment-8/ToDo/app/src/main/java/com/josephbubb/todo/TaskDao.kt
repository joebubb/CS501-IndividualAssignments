package com.josephbubb.todo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    // Get all tasks, ordered by ID (newest first)
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>

    // Get only pending (not completed) tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY id DESC")
    fun getPendingTasks(): Flow<List<Task>>

    // Get only completed tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY id DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    // Insert a new task or replace if ID conflicts (though ID is auto-gen)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    // Update an existing task (used for completion status and potential text edits)
    @Update
    suspend fun update(task: Task)

    // Delete a task
    @Delete
    suspend fun delete(task: Task)
}