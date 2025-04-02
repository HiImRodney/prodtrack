package com.productivity.tracker.data.database.dao

import androidx.room.*
import com.productivity.tracker.data.database.entity.Habit
import com.productivity.tracker.data.database.entity.HabitLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HabitDao {
    @Insert
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Long): Flow<Habit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(habitLog: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitLog(habitId: Long, date: LocalDate)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY date DESC")
    fun getHabitLogsForHabit(habitId: Long): Flow<List<HabitLog>>

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getHabitLogsForDate(date: LocalDate): Flow<List<HabitLog>>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE date >= :startDate AND date <= :endDate AND habitId = :habitId")
    fun getHabitCompletionCountForPeriod(habitId: Long, startDate: LocalDate, endDate: LocalDate): Flow<Int>

    @Query("SELECT SUM(pointsEarned) FROM habit_logs")
    fun getTotalPointsFromHabits(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM habit_logs")
    fun getTotalHabitCompletions(): Flow<Int>

    @Query("SELECT COUNT(*) FROM habit_logs WHERE date = :date")
    suspend fun getHabitLogsCountForDate(date: LocalDate): Int
}
