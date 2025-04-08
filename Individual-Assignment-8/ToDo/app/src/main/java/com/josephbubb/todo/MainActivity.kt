package com.josephbubb.todo

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
// Import your Task, TaskViewModel, FilterType etc. if they are in different packages

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoAppTheme { // Replace with your actual theme name if different
                val context = LocalContext.current
                val viewModel: TaskViewModel = viewModel(
                    factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
                        context.applicationContext as Application
                    )
                )
                TaskScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    // Observe the list of tasks and the current filter state from the ViewModel
    val tasks by viewModel.tasks.collectAsState()
    val currentFilter by viewModel.filterState.collectAsState()

    // State for the input field
    var taskDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Simple To-Do List") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp) // Add padding around the content
                .fillMaxSize()
        ) {
            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("New Task Description") },
                    modifier = Modifier.weight(1f), // Take available space
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addTask(taskDescription)
                        taskDescription = "" // Clear input after adding
                    },
                    enabled = taskDescription.isNotBlank() // Enable only if text is entered
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Distribute buttons
            ) {
                FilterButton("All", currentFilter == FilterType.ALL) {
                    viewModel.setFilter(FilterType.ALL)
                }
                FilterButton("Pending", currentFilter == FilterType.PENDING) {
                    viewModel.setFilter(FilterType.PENDING)
                }
                FilterButton("Completed", currentFilter == FilterType.COMPLETED) {
                    viewModel.setFilter(FilterType.COMPLETED)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider() // Visual separator
            Spacer(modifier = Modifier.height(8.dp))

            // Task List
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks ${
                        when(currentFilter) {
                            FilterType.ALL -> ""
                            FilterType.PENDING -> "pending"
                            FilterType.COMPLETED -> "completed"
                        }
                    }")
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tasks, key = { it.id }) { task -> // Use task ID as key for performance
                        TaskItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(text)
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggleComplete() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.description,
            modifier = Modifier.weight(1f), // Take available space
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            color = if (task.isCompleted) MaterialTheme.colorScheme.outline else LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}

// Remember to define your Theme (e.g., ToDoAppTheme) similar to the examples
@Composable
fun ToDoAppTheme(content: @Composable () -> Unit) {
    // Basic Material 3 theme setup, adapt as needed from RoomCRUD/other examples
    MaterialTheme(
        colorScheme = lightColorScheme(), // Or use darkColorScheme(), dynamic schemes etc.
        content = content
    )
}