package com.productivity.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivity.tracker.data.database.entity.Task
import com.productivity.tracker.data.repository.TaskRepository
import com.productivity.tracker.util.PointsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val pointsCalculator: PointsCalculator
) : ViewModel() {

    private val _activeTasks = MutableStateFlow<List<Task>>(emptyList())
    val activeTasks: StateFlow<List<Task>> = _activeTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<Task>>(emptyList())
    val completedTasks: StateFlow<List<Task>> = _completedTasks.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            try {
                taskRepository.getActiveTasks().collect { tasks ->
                    _activeTasks.value = tasks
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load active tasks: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                taskRepository.getCompletedTasks().collect { tasks ->
                    _completedTasks.value = tasks
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load completed tasks: ${e.message}"
            }
        }
    }

    fun addTask(title: String, description: String, durationMinutes: Int) {
        if (title.isBlank()) {
            _errorMessage.value = "Task title cannot be empty"
            return
        }

        if (durationMinutes !in listOf(15, 30, 60)) {
            _errorMessage.value = "Task duration must be 15, 30, or 60 minutes"
            return
        }

        viewModelScope.launch {
            try {
                val task = Task(
                    title = title,
                    description = description,
                    durationMinutes = durationMinutes
                )
                taskRepository.addTask(task)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add task: ${e.message}"
            }
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            try {
                val points = pointsCalculator.calculateTaskPoints(task.durationMinutes)
                taskRepository.completeTask(task, points)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to complete task: ${e.message}"
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.deleteTask(task)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
