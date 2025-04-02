package com.productivity.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivity.tracker.data.database.entity.StepCount
import com.productivity.tracker.data.repository.StepCountRepository
import com.productivity.tracker.util.PointsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StepCountViewModel @Inject constructor(
    private val stepCountRepository: StepCountRepository,
    private val pointsCalculator: PointsCalculator
) : ViewModel() {

    private val _todayStepCount = MutableStateFlow<StepCount?>(null)
    val todayStepCount: StateFlow<StepCount?> = _todayStepCount.asStateFlow()

    private val _recentStepCounts = MutableStateFlow<List<StepCount>>(emptyList())
    val recentStepCounts: StateFlow<List<StepCount>> = _recentStepCounts.asStateFlow()

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadStepCounts()
    }

    private fun loadStepCounts() {
        viewModelScope.launch {
            try {
                stepCountRepository.getTodayStepCount().collect { stepCount ->
                    _todayStepCount.value = stepCount ?: StepCount(
                        date = LocalDate.now(),
                        steps = 0,
                        goal = 10000 // Default step goal
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load today's step count: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                stepCountRepository.getAllStepCounts().collect { stepCounts ->
                    _recentStepCounts.value = stepCounts
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recent step counts: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                stepCountRepository.getTotalSteps().collect { steps ->
                    _totalSteps.value = steps
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load total steps: ${e.message}"
            }
        }
    }

    fun addSteps(stepsToAdd: Int, goal: Int = 10000) {
        if (stepsToAdd <= 0) {
            _errorMessage.value = "Steps must be greater than 0"
            return
        }

        if (goal <= 0) {
            _errorMessage.value = "Goal must be greater than 0"
            return
        }

        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val currentStepCount = _todayStepCount.value
                
                val currentSteps = currentStepCount?.steps ?: 0
                val newTotalSteps = currentSteps + stepsToAdd
                
                // Check if the step goal is newly achieved with this addition
                val wasGoalAchieved = currentStepCount?.steps ?: 0 >= goal
                val isGoalNowAchieved = newTotalSteps >= goal
                
                // Calculate points only if goal is newly achieved
                val pointsEarned = if (!wasGoalAchieved && isGoalNowAchieved) {
                    pointsCalculator.calculateStepGoalPoints()
                } else {
                    currentStepCount?.pointsEarned ?: 0
                }
                
                stepCountRepository.addSteps(today, stepsToAdd, goal, pointsEarned)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add steps: ${e.message}"
            }
        }
    }

    fun updateStepGoal(goal: Int) {
        if (goal <= 0) {
            _errorMessage.value = "Goal must be greater than 0"
            return
        }

        viewModelScope.launch {
            try {
                val currentStepCount = _todayStepCount.value
                if (currentStepCount != null) {
                    stepCountRepository.updateStepCount(
                        currentStepCount.copy(goal = goal)
                    )
                    _errorMessage.value = null
                } else {
                    // Create a new step count for today with the goal
                    stepCountRepository.addSteps(
                        LocalDate.now(),
                        0,
                        goal
                    )
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update step goal: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
