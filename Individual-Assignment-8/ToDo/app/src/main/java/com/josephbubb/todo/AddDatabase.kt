package com.josephbubb.todo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false) // Added exportSchema = false for simplicity
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao // Provide the DAO

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "task_database" // Database file name
                )
                    // .fallbackToDestructiveMigration() // Optional: Add if you change schema later without migration
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}