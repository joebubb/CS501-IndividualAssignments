package com.jbubb.diary

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiaryScreen()
        }
    }
}

@Composable
fun DiaryScreen() {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val diaryFileManager = remember { DiaryFileManager(context) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var diaryEntryText by remember { mutableStateOf("") }
    val fontSize by preferencesManager.fontSizeFlow.collectAsState(initial = PreferencesManager.DEFAULT_FONT_SIZE)
    val coroutineScope = rememberCoroutineScope()
    var statusMessage by remember { mutableStateOf("") }

    LaunchedEffect(selectedDate) {
        statusMessage = "Loading..."
        diaryEntryText = diaryFileManager.loadEntry(selectedDate)
        statusMessage = if (diaryEntryText.startsWith("Error")) diaryEntryText else "Entry loaded for ${selectedDate.format(DateTimeFormatter.ISO_DATE)}"
        if(diaryEntryText.isEmpty() && !statusMessage.startsWith("Error")) {
            statusMessage = "No entry for ${selectedDate.format(DateTimeFormatter.ISO_DATE)}"
        }
    }

    fun showDatePicker() {
        val year = selectedDate.year
        val month = selectedDate.monthValue - 1
        val day = selectedDate.dayOfMonth

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val newDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDayOfMonth)
            if (newDate != selectedDate) {
                selectedDate = newDate
            }
        }, year, month, day).show()
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date: ${selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))}",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(onClick = { showDatePicker() }) {
                    Text("Select Date")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (statusMessage.isNotEmpty()) {
                Text(statusMessage, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = diaryEntryText,
                onValueChange = { diaryEntryText = it },
                label = { Text("Write here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(fontSize = fontSize.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Font Size: $fontSize sp")
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        preferencesManager.setFontSize( (fontSize - 2).coerceAtLeast(10) )
                    }
                }, enabled = fontSize > 10) { Text("-") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        preferencesManager.setFontSize( (fontSize + 2).coerceAtMost(32) )
                    }
                }, enabled = fontSize < 32) { Text("+") }
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    coroutineScope.launch {
                        val success = diaryFileManager.saveEntry(selectedDate, diaryEntryText)
                        statusMessage = if(success) "Entry saved for ${selectedDate.format(DateTimeFormatter.ISO_DATE)}" else "Failed to save entry"
                    }
                }) {
                    Text("Save Entry")
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val deleted = diaryFileManager.deleteEntry(selectedDate)
                            if(deleted){
                                diaryEntryText = ""
                                statusMessage = "Entry deleted for ${selectedDate.format(DateTimeFormatter.ISO_DATE)}"
                            } else {
                                statusMessage = "Failed to delete entry (or no entry exists)"
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Entry")
                }
            }
        }
    }
}