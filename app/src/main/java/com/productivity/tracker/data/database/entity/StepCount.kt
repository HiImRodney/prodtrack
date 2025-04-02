package com.productivity.tracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "step_counts")
data class StepCount(
    @PrimaryKey
    val date: LocalDate,
    val steps: Int,
    val goal: Int,
    val pointsEarned: Int = 0,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
