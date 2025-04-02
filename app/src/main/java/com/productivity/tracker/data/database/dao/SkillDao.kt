package com.productivity.tracker.data.database.dao

import androidx.room.*
import com.productivity.tracker.data.database.entity.Skill
import com.productivity.tracker.data.database.entity.SkillLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface SkillDao {
    @Insert
    suspend fun insertSkill(skill: Skill): Long

    @Update
    suspend fun updateSkill(skill: Skill)

    @Delete
    suspend fun deleteSkill(skill: Skill)

    @Query("SELECT * FROM skills ORDER BY name ASC")
    fun getAllSkills(): Flow<List<Skill>>

    @Query("SELECT * FROM skills WHERE id = :skillId")
    fun getSkillById(skillId: Long): Flow<Skill?>

    @Insert
    suspend fun insertSkillLog(skillLog: SkillLog): Long

    @Delete
    suspend fun deleteSkillLog(skillLog: SkillLog)

    @Query("SELECT * FROM skill_logs WHERE skillId = :skillId ORDER BY date DESC")
    fun getSkillLogsForSkill(skillId: Long): Flow<List<SkillLog>>

    @Query("SELECT * FROM skill_logs WHERE date = :date")
    fun getSkillLogsForDate(date: LocalDate): Flow<List<SkillLog>>

    @Query("SELECT SUM(minutes) FROM skill_logs WHERE skillId = :skillId")
    fun getTotalMinutesForSkill(skillId: Long): Flow<Int?>

    @Query("UPDATE skills SET totalMinutes = totalMinutes + :minutes WHERE id = :skillId")
    suspend fun updateSkillTotalMinutes(skillId: Long, minutes: Int)

    @Query("SELECT SUM(pointsEarned) FROM skill_logs")
    fun getTotalPointsFromSkills(): Flow<Int?>

    @Query("SELECT SUM(minutes) FROM skill_logs")
    fun getTotalSkillMinutes(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM skill_logs WHERE date = :date")
    suspend fun getSkillLogsCountForDate(date: LocalDate): Int
}
