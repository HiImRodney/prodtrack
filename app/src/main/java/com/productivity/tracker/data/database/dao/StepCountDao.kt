package com.productivity.tracker.data.database.dao

import androidx.room.*
import com.productivity.tracker.data.database.entity.StepCount
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StepCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepCount(stepCount: StepCount)

    @Update
    suspend fun updateStepCount(stepCount: StepCount)

    @Query("SELECT * FROM step_counts ORDER BY date DESC")
    fun getAllStepCounts(): Flow<List<StepCount>>

    @Query("SELECT * FROM step_counts WHERE date = :date")
    fun getStepCountForDate(date: LocalDate): Flow<StepCount?>

    @Query("SELECT SUM(steps) FROM step_counts")
    fun getTotalSteps(): Flow<Int?>

    @Query("SELECT SUM(pointsEarned) FROM step_counts")
    fun getTotalPointsFromSteps(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM step_counts WHERE date = :date AND steps >= goal")
    suspend fun hasAchievedStepGoalForDate(date: LocalDate): Int
}
