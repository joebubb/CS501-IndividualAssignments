package com.josephbubb.todo

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Enum to represent filter states
enum class FilterType { ALL, PENDING, COMPLETED }

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).taskDao()

    // StateFlow to hold the current filter type
    private val _filterState = MutableStateFlow(FilterType.ALL)
    val filterState: StateFlow<FilterType> = _filterState

    // Use flatMapLatest to reactively switch the Flow based on the filter state
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = _filterState.flatMapLatest { filter ->
        when (filter) {
            FilterType.ALL -> dao.getAllTasks()
            FilterType.PENDING -> dao.getPendingTasks()
            FilterType.COMPLETED -> dao.getCompletedTasks()
        }
    }.stateIn( // Convert Flow to StateFlow for easier Compose collection
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keep flow active 5s after last subscriber
        initialValue = emptyList() // Initial value before first emission
    )

    // Function to add a new task
    fun addTask(description: String) {
        if (description.isBlank()) return // Don't add empty tasks
        viewModelScope.launch {
            dao.insert(Task(description = description, isCompleted = false))
        }
    }

    // Function to update a task (mainly for toggling completion)
    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.update(task)
        }
    }

    // Convenience function to toggle completion status
    fun toggleCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        updateTask(updatedTask)
    }

    // Function to delete a task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    // Function to change the current filter
    fun setFilter(filterType: FilterType) {
        _filterState.value = filterType
    }
}