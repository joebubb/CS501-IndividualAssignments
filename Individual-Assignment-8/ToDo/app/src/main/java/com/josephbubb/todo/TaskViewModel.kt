package com.josephbubb.todo

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FilterType { ALL, PENDING, COMPLETED }

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).taskDao()

    private val _filterState = MutableStateFlow(FilterType.ALL)
    val filterState: StateFlow<FilterType> = _filterState

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = _filterState.flatMapLatest { filter ->
        when (filter) {
            FilterType.ALL -> dao.getAllTasks()
            FilterType.PENDING -> dao.getPendingTasks()
            FilterType.COMPLETED -> dao.getCompletedTasks()
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = emptyList(),
        started = SharingStarted.WhileSubscribed(5000),
    )

    fun addTask(description: String) {
        if (description.isBlank()) return
        viewModelScope.launch {
            dao.insert(Task(description = description, isCompleted = false))
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.update(task)
        }
    }

    fun toggleCompletion(task: Task) {
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        updateTask(updatedTask)
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.delete(task)
        }
    }

    fun setFilter(filterType: FilterType) {
        _filterState.value = filterType
    }
}