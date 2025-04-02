package com.productivity.tracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "nidge_cards")
data class NidgeCard(
    @PrimaryKey
    val weekStartDate: LocalDate, // First day of the week
    val used: Boolean = false,
    val usedDate: LocalDate? = null,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "streak_records")
data class StreakRecord(
    @PrimaryKey
    val date: LocalDate,
    val hasActivity: Boolean,
    val nidgeCardUsed: Boolean = false,
    val currentStreak: Int = 0,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
