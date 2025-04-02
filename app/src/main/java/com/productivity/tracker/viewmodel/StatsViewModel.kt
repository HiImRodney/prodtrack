package com.productivity.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivity.tracker.data.database.entity.StreakRecord
import com.productivity.tracker.data.repository.*
import com.productivity.tracker.util.StreakTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val skillRepository: SkillRepository,
    private val stepCountRepository: StepCountRepository,
    private val nidgeCardRepository: NidgeCardRepository,
    private val streakTracker: StreakTracker
) : ViewModel() {

    private val _totalPoints = MutableStateFlow(0)
    val totalPoints: StateFlow<Int> = _totalPoints.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _longestStreak = MutableStateFlow(0)
    val longestStreak: StateFlow<Int> = _longestStreak.asStateFlow()

    private val _tasksCompleted = MutableStateFlow(0)
    val tasksCompleted: StateFlow<Int> = _tasksCompleted.asStateFlow()

    private val _habitsCompleted = MutableStateFlow(0)
    val habitsCompleted: StateFlow<Int> = _habitsCompleted.asStateFlow()

    private val _totalSkillMinutes = MutableStateFlow(0)
    val totalSkillMinutes: StateFlow<Int> = _totalSkillMinutes.asStateFlow()

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _streakRecords = MutableStateFlow<List<StreakRecord>>(emptyList())
    val streakRecords: StateFlow<List<StreakRecord>> = _streakRecords.asStateFlow()

    private val _hasActivityToday = MutableStateFlow(false)
    val hasActivityToday: StateFlow<Boolean> = _hasActivityToday.asStateFlow()

    private val _canUseNidgeCardToday = MutableStateFlow(false)
    val canUseNidgeCardToday: StateFlow<Boolean> = _canUseNidgeCardToday.asStateFlow()

    private val _nidgeCardUsedThisWeek = MutableStateFlow(false)
    val nidgeCardUsedThisWeek: StateFlow<Boolean> = _nidgeCardUsedThisWeek.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadStats()
        checkNidgeCardStatus()
        checkTodayActivity()
    }

    private fun loadStats() {
        viewModelScope.launch {
            try {
                combine(
                    taskRepository.getTotalPointsFromTasks().map { it ?: 0 },
                    habitRepository.getTotalPointsFromHabits().map { it ?: 0 },
                    skillRepository.getTotalPointsFromSkills().map { it ?: 0 },
                    stepCountRepository.getTotalPointsFromSteps().map { it ?: 0 }
                ) { taskPoints, habitPoints, skillPoints, stepPoints ->
                    taskPoints + habitPoints + skillPoints + stepPoints
                }.collect { total ->
                    _totalPoints.value = total
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load total points: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                nidgeCardRepository.getCurrentStreak().collect { streak ->
                    _currentStreak.value = streak ?: 0
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load current streak: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                nidgeCardRepository.getLongestStreak().collect { streak ->
                    _longestStreak.value = streak ?: 0
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load longest streak: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                taskRepository.getCompletedTasksCount().collect { count ->
                    _tasksCompleted.value = count
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load tasks completed: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                habitRepository.getTotalHabitCompletions().collect { count ->
                    _habitsCompleted.value = count
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load habits completed: ${e.message}"
            }
        }

        viewModelScope.launch {
            try {
                skillRepository.getTotalSkillMinutes().collect { minutes ->
                    _totalSkillMinutes.value = minutes ?: 0
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load total skill minutes: ${e.message}"
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

        viewModelScope.launch {
            try {
                nidgeCardRepository.getAllStreakRecords().collect { records ->
                    _streakRecords.value = records
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load streak records: ${e.message}"
            }
        }
    }

    private fun checkNidgeCardStatus() {
        viewModelScope.launch {
            try {
                nidgeCardRepository.getNidgeCardForCurrentWeek().collect { nidgeCard ->
                    _nidgeCardUsedThisWeek.value = nidgeCard?.used == true
                    updateCanUseNidgeCard()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to check nidge card status: ${e.message}"
            }
        }
    }

    private fun checkTodayActivity() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val tasksCount = taskRepository.getCompletedTasksCountForDate(today)
                val habitsCount = habitRepository.getHabitLogsCountForDate(today)
                val skillsCount = skillRepository.getSkillLogsCountForDate(today)
                val stepGoalMet = stepCountRepository.hasAchievedStepGoalForDate(today)

                val hasActivity = streakTracker.hasActivityToday(
                    tasksCount,
                    habitsCount,
                    skillsCount,
                    stepGoalMet > 0
                )
                _hasActivityToday.value = hasActivity
                updateCanUseNidgeCard()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to check today's activity: ${e.message}"
            }
        }
    }
    
    private fun updateCanUseNidgeCard() {
        // Can use nidge card if:
        // 1. It hasn't been used this week
        // 2. There is no activity today yet
        // 3. There was activity yesterday (to maintain streak)
        _canUseNidgeCardToday.value = !_nidgeCardUsedThisWeek.value && 
                                      !_hasActivityToday.value && 
                                      _currentStreak.value > 0
    }

    fun useNidgeCard() {
        if (!_canUseNidgeCardToday.value) {
            _errorMessage.value = "Cannot use Nidge Card today"
            return
        }

        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                val currentWeekStart = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                
                // Get or create nidge card for this week
                val nidgeCard = nidgeCardRepository.getNidgeCardForCurrentWeek()
                    .firstOrNull() ?: run {
                        nidgeCardRepository.createNidgeCardForWeek(currentWeekStart)
                        nidgeCardRepository.getNidgeCardForCurrentWeek().first()
                    }
                
                if (nidgeCard == null) {
                    _errorMessage.value = "Failed to get or create Nidge Card"
                    return@launch
                }
                
                if (nidgeCard.used) {
                    _errorMessage.value = "Nidge Card already used this week"
                    return@launch
                }
                
                // Use the nidge card
                nidgeCardRepository.useNidgeCard(nidgeCard, today)
                
                // Create a streak record for today with nidge card used
                val latestStreak = nidgeCardRepository.getLatestStreakRecord().firstOrNull()
                val currentStreakValue = latestStreak?.currentStreak ?: 0
                
                nidgeCardRepository.recordStreakDay(
                    date = today,
                    hasActivity = false,
                    nidgeCardUsed = true,
                    currentStreak = currentStreakValue
                )
                
                _nidgeCardUsedThisWeek.value = true
                _canUseNidgeCardToday.value = false
                _errorMessage.value = null
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to use Nidge Card: ${e.message}"
            }
        }
    }

    fun updateStreakForToday() {
        viewModelScope.launch {
            try {
                val today = LocalDate.now()
                checkTodayActivity()
                
                if (_hasActivityToday.value) {
                    // Get the latest streak record
                    val latestStreak = nidgeCardRepository.getLatestStreakRecord().firstOrNull()
                    
                    // Calculate the new streak value
                    val newStreakValue = if (latestStreak == null) {
                        // First streak record
                        1
                    } else if (latestStreak.date == today.minusDays(1) && 
                              (latestStreak.hasActivity || latestStreak.nidgeCardUsed)) {
                        // Continuing the streak from yesterday
                        latestStreak.currentStreak + 1
                    } else if (latestStreak.date == today) {
                        // Already recorded today
                        latestStreak.currentStreak
                    } else {
                        // Streak broken or started new
                        1
                    }
                    
                    // Record today's streak
                    nidgeCardRepository.recordStreakDay(
                        date = today,
                        hasActivity = true,
                        nidgeCardUsed = false,
                        currentStreak = newStreakValue
                    )
                    
                    _currentStreak.value = newStreakValue
                    // Update longest streak if needed
                    if (newStreakValue > _longestStreak.value) {
                        _longestStreak.value = newStreakValue
                    }
                }
                
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update streak: ${e.message}"
            }
        }
    }

    fun refresh() {
        loadStats()
        checkNidgeCardStatus()
        checkTodayActivity()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
