package com.productivity.tracker.data.repository

import com.productivity.tracker.data.database.dao.HabitDao
import com.productivity.tracker.data.database.entity.Habit
import com.productivity.tracker.data.database.entity.HabitLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class HabitRepository @Inject constructor(
    private val habitDao: HabitDao
) {
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    
    fun getHabitById(habitId: Long): Flow<Habit?> = habitDao.getHabitById(habitId)
    
    fun getHabitLogsForHabit(habitId: Long): Flow<List<HabitLog>> = habitDao.getHabitLogsForHabit(habitId)
    
    fun getHabitLogsForDate(date: LocalDate): Flow<List<HabitLog>> = habitDao.getHabitLogsForDate(date)
    
    fun getHabitCompletionCountForPeriod(habitId: Long, startDate: LocalDate, endDate: LocalDate): Flow<Int> = 
        habitDao.getHabitCompletionCountForPeriod(habitId, startDate, endDate)
    
    fun getTotalPointsFromHabits(): Flow<Int?> = habitDao.getTotalPointsFromHabits()
    
    fun getTotalHabitCompletions(): Flow<Int> = habitDao.getTotalHabitCompletions()
    
    suspend fun getHabitLogsCountForDate(date: LocalDate): Int = habitDao.getHabitLogsCountForDate(date)
    
    suspend fun addHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    
    suspend fun logHabit(habitId: Long, date: LocalDate, pointsEarned: Int) {
        habitDao.insertHabitLog(
            HabitLog(
                habitId = habitId,
                date = date,
                pointsEarned = pointsEarned
            )
        )
    }
    
    suspend fun deleteHabitLog(habitId: Long, date: LocalDate) = habitDao.deleteHabitLog(habitId, date)
}
