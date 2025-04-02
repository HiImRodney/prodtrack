package com.productivity.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivity.tracker.data.database.entity.Habit
import com.productivity.tracker.data.database.entity.HabitLog
import com.productivity.tracker.data.repository.HabitRepository
import com.productivity.tracker.util.DateUtils
import com.productivity.tracker.util.PointsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val pointsCalculator: PointsCalculator
) : ViewModel() {

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _habitLogs = MutableStateFlow<Map<Long, List<HabitLog>>>(emptyMap())
    val habitLogs: StateFlow<Map<Long, List<HabitLog>>> = _habitLogs.asStateFlow()

    private val _progressMap = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val progressMap: StateFlow<Map<Long, Int>> = _progressMap.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            try {
                habitRepository.getAllHabits().collect { habitsList ->
                    _habits.value = habitsList
                    updateHabitLogs(habitsList)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load habits: ${e.message}"
            }
        }
    }

    private fun updateHabitLogs(habits: List<Habit>) {
        habits.forEach { habit ->
            viewModelScope.launch {
                try {
                    habitRepository.getHabitLogsForHabit(habit.id).collect { logs ->
                        _habitLogs.update { currentLogs ->
                            val mutableLogs = currentLogs.toMutableMap()
                            mutableLogs[habit.id] = logs
                            mutableLogs
                        }
                        updateProgress(habit)
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load habit logs: ${e.message}"
                }
            }
        }
    }

    private fun updateProgress(habit: Habit) {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val periodStartDate = today.minusWeeks(habit.periodWeeks.toLong())
                
                habitRepository.getHabitCompletionCountForPeriod(
                    habit.id, 
                    periodStartDate, 
                    today
                ).collect { completionCount ->
                    _progressMap.update { currentMap ->
                        val mutableMap = currentMap.toMutableMap()
                        mutableMap[habit.id] = completionCount
                        mutableMap
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update progress: ${e.message}"
            }
        }
    }

    fun addHabit(name: String, description: String, targetCount: Int, periodWeeks: Int) {
        if (name.isBlank()) {
            _errorMessage.value = "Habit name cannot be empty"
            return
        }

        if (targetCount <= 0) {
            _errorMessage.value = "Target count must be greater than 0"
            return
        }

        if (periodWeeks <= 0) {
            _errorMessage.value = "Period weeks must be greater than 0"
            return
        }

        viewModelScope.launch {
            try {
                val habit = Habit(
                    name = name,
                    description = description,
                    targetCount = targetCount,
                    periodWeeks = periodWeeks
                )
                habitRepository.addHabit(habit)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add habit: ${e.message}"
            }
        }
    }

    fun logHabit(habitId: Long, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            try {
                val points = pointsCalculator.calculateHabitPoints()
                habitRepository.logHabit(habitId, date, points)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to log habit: ${e.message}"
            }
        }
    }

    fun deleteHabitLog(habitId: Long, date: LocalDate) {
        viewModelScope.launch {
            try {
                habitRepository.deleteHabitLog(habitId, date)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete habit log: ${e.message}"
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                habitRepository.deleteHabit(habit)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete habit: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
