package com.productivity.tracker.data.repository

import com.productivity.tracker.data.database.dao.TaskDao
import com.productivity.tracker.data.database.entity.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    
    fun getActiveTasks(): Flow<List<Task>> = taskDao.getActiveTasks()
    
    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getCompletedTasks()
    
    fun getTaskById(taskId: Long): Flow<Task?> = taskDao.getTaskById(taskId)
    
    fun getCompletedTasksCount(): Flow<Int> = taskDao.getCompletedTasksCount()
    
    fun getTotalPointsFromTasks(): Flow<Int?> = taskDao.getTotalPointsFromTasks()
    
    fun getTasksCompletedBetween(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Task>> = 
        taskDao.getTasksCompletedBetween(startDate, endDate)
    
    suspend fun getCompletedTasksCountForDate(date: LocalDate): Int = 
        taskDao.getCompletedTasksCountForDate(date)
    
    suspend fun addTask(task: Task): Long = taskDao.insertTask(task)
    
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    suspend fun completeTask(task: Task, pointsEarned: Int) {
        taskDao.updateTask(
            task.copy(
                isCompleted = true,
                completedAt = LocalDateTime.now(),
                pointsEarned = pointsEarned
            )
        )
    }
}
