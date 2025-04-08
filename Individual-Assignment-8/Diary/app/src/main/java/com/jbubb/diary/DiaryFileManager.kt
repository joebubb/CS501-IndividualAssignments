package com.jbubb.diary

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DiaryFileManager(private val context: Context) {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val fileExtension = ".txt"

    private fun getFileName(date: LocalDate): String {
        return "${date.format(dateFormatter)}$fileExtension"
    }

    suspend fun saveEntry(date: LocalDate, content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val filename = getFileName(date)
            context.openFileOutput(filename, Context.MODE_PRIVATE).use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun loadEntry(date: LocalDate): String = withContext(Dispatchers.IO) {
        try {
            val filename = getFileName(date)
            context.openFileInput(filename).bufferedReader().useLines { lines ->
                lines.joinToString("\n")
            }
        } catch (e: FileNotFoundException) {
            ""
        } catch (e: Exception) {
            e.printStackTrace()
            "Error loading entry."
        }
    }

    suspend fun deleteEntry(date: LocalDate): Boolean = withContext(Dispatchers.IO) {
        try {
            val filename = getFileName(date)
            context.deleteFile(filename)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}