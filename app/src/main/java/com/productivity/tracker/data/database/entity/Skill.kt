package com.productivity.tracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val totalMinutes: Int = 0
)

@Entity(tableName = "skill_logs")
data class SkillLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val skillId: Long,
    val date: LocalDate,
    val minutes: Int,
    val pointsEarned: Int = 0,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
