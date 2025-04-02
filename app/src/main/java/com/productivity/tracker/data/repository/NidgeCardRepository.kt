package com.productivity.tracker.data.repository

import com.productivity.tracker.data.database.dao.NidgeCardDao
import com.productivity.tracker.data.database.entity.NidgeCard
import com.productivity.tracker.data.database.entity.StreakRecord
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class NidgeCardRepository @Inject constructor(
    private val nidgeCardDao: NidgeCardDao
) {
    fun getNidgeCardForCurrentWeek(): Flow<NidgeCard?> {
        val currentDate = LocalDate.now()
        val startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return nidgeCardDao.getNidgeCardForWeek(startOfWeek)
    }
    
    fun getLatestUnusedNidgeCard(): Flow<NidgeCard?> = 
        nidgeCardDao.getLatestUnusedNidgeCard(LocalDate.now())
    
    fun getAllStreakRecords(): Flow<List<StreakRecord>> = nidgeCardDao.getAllStreakRecords()
    
    fun getStreakRecordForDate(date: LocalDate): Flow<StreakRecord?> = 
        nidgeCardDao.getStreakRecordForDate(date)
    
    fun getLatestStreakRecord(): Flow<StreakRecord?> = nidgeCardDao.getLatestStreakRecord()
    
    fun getLongestStreak(): Flow<Int?> = nidgeCardDao.getLongestStreak()
    
    fun getCurrentStreak(): Flow<Int?> = nidgeCardDao.getCurrentStreak()
    
    suspend fun createNidgeCardForWeek(weekStartDate: LocalDate) {
        nidgeCardDao.insertNidgeCard(
            NidgeCard(
                weekStartDate = weekStartDate
            )
        )
    }
    
    suspend fun useNidgeCard(nidgeCard: NidgeCard, useDate: LocalDate) {
        nidgeCardDao.updateNidgeCard(
            nidgeCard.copy(
                used = true,
                usedDate = useDate
            )
        )
    }
    
    suspend fun recordStreakDay(date: LocalDate, hasActivity: Boolean, nidgeCardUsed: Boolean, currentStreak: Int) {
        nidgeCardDao.insertStreakRecord(
            StreakRecord(
                date = date,
                hasActivity = hasActivity,
                nidgeCardUsed = nidgeCardUsed,
                currentStreak = currentStreak
            )
        )
    }
}
