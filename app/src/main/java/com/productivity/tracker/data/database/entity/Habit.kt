package com.productivity.tracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val targetCount: Int, // E.g. 12 visits
    val periodWeeks: Int, // E.g. 4 weeks
    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity(tableName = "habit_logs", primaryKeys = ["habitId", "date"])
data class HabitLog(
    val habitId: Long,
    val date: LocalDate,
    val completed: Boolean = true,
    val pointsEarned: Int = 0,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
