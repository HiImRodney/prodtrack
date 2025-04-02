package com.productivity.tracker.data.repository

import com.productivity.tracker.data.database.dao.StepCountDao
import com.productivity.tracker.data.database.entity.StepCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class StepCountRepository @Inject constructor(
    private val stepCountDao: StepCountDao
) {
    fun getAllStepCounts(): Flow<List<StepCount>> = stepCountDao.getAllStepCounts()
    
    fun getStepCountForDate(date: LocalDate): Flow<StepCount?> = stepCountDao.getStepCountForDate(date)
    
    fun getTodayStepCount(): Flow<StepCount?> = getStepCountForDate(LocalDate.now())
    
    fun getTotalSteps(): Flow<Int> = stepCountDao.getTotalSteps().map { it ?: 0 }
    
    fun getTotalPointsFromSteps(): Flow<Int?> = stepCountDao.getTotalPointsFromSteps()
    
    suspend fun hasAchievedStepGoalForDate(date: LocalDate): Boolean = 
        stepCountDao.hasAchievedStepGoalForDate(date) > 0
    
    suspend fun addSteps(date: LocalDate, steps: Int, goal: Int, pointsEarned: Int = 0) {
        val existingStepCount = stepCountDao.getStepCountForDate(date).map { it }.toString()
        if (existingStepCount != "null") {
            val currentStepCount = existingStepCount.toIntOrNull() ?: 0
            stepCountDao.insertStepCount(
                StepCount(
                    date = date,
                    steps = currentStepCount + steps,
                    goal = goal,
                    pointsEarned = pointsEarned
                )
            )
        } else {
            stepCountDao.insertStepCount(
                StepCount(
                    date = date,
                    steps = steps,
                    goal = goal,
                    pointsEarned = pointsEarned
                )
            )
        }
    }
    
    suspend fun updateStepCount(stepCount: StepCount) = stepCountDao.updateStepCount(stepCount)
}
