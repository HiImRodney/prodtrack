package com.productivity.tracker.data.database.dao

import androidx.room.*
import com.productivity.tracker.data.database.entity.NidgeCard
import com.productivity.tracker.data.database.entity.StreakRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface NidgeCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNidgeCard(nidgeCard: NidgeCard)

    @Update
    suspend fun updateNidgeCard(nidgeCard: NidgeCard)

    @Query("SELECT * FROM nidge_cards WHERE weekStartDate = :weekStartDate")
    fun getNidgeCardForWeek(weekStartDate: LocalDate): Flow<NidgeCard?>

    @Query("SELECT * FROM nidge_cards WHERE used = 0 AND weekStartDate <= :currentDate ORDER BY weekStartDate DESC LIMIT 1")
    fun getLatestUnusedNidgeCard(currentDate: LocalDate): Flow<NidgeCard?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreakRecord(streakRecord: StreakRecord)

    @Query("SELECT * FROM streak_records ORDER BY date DESC")
    fun getAllStreakRecords(): Flow<List<StreakRecord>>

    @Query("SELECT * FROM streak_records WHERE date = :date")
    fun getStreakRecordForDate(date: LocalDate): Flow<StreakRecord?>

    @Query("SELECT * FROM streak_records ORDER BY date DESC LIMIT 1")
    fun getLatestStreakRecord(): Flow<StreakRecord?>

    @Query("SELECT MAX(currentStreak) FROM streak_records")
    fun getLongestStreak(): Flow<Int?>

    @Query("SELECT currentStreak FROM streak_records ORDER BY date DESC LIMIT 1")
    fun getCurrentStreak(): Flow<Int?>
}
