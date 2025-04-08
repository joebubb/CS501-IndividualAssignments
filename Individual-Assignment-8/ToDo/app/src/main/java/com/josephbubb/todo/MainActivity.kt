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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

@OptIn(ExperimentalMaterial3Api::class) // needed for the top app bar
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val currentFilter by viewModel.filterState.collectAsState()

    var taskDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ToDo List") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("New Task") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addTask(taskDescription)
                        taskDescription = ""
                    },
                    enabled = taskDescription.isNotBlank()
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
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
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

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
                    items(tasks, key = { it.id }) { task ->
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
            modifier = Modifier.weight(1f),
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            color = if (task.isCompleted) MaterialTheme.colorScheme.outline else LocalContentColor.current
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}