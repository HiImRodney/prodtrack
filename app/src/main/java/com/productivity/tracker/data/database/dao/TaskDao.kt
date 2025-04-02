package com.productivity.tracker.data.database.dao

import androidx.room.*
import com.productivity.tracker.data.database.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasksCount(): Flow<Int>

    @Query("SELECT SUM(pointsEarned) FROM tasks")
    fun getTotalPointsFromTasks(): Flow<Int?>

    @Query("SELECT * FROM tasks WHERE completedAt >= :startDate AND completedAt <= :endDate ORDER BY completedAt DESC")
    fun getTasksCompletedBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks WHERE date(completedAt) = :date AND isCompleted = 1")
    suspend fun getCompletedTasksCountForDate(date: LocalDate): Int
}
