package com.productivity.tracker.data.repository

import com.productivity.tracker.data.database.dao.SkillDao
import com.productivity.tracker.data.database.entity.Skill
import com.productivity.tracker.data.database.entity.SkillLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class SkillRepository @Inject constructor(
    private val skillDao: SkillDao
) {
    fun getAllSkills(): Flow<List<Skill>> = skillDao.getAllSkills()
    
    fun getSkillById(skillId: Long): Flow<Skill?> = skillDao.getSkillById(skillId)
    
    fun getSkillLogsForSkill(skillId: Long): Flow<List<SkillLog>> = skillDao.getSkillLogsForSkill(skillId)
    
    fun getSkillLogsForDate(date: LocalDate): Flow<List<SkillLog>> = skillDao.getSkillLogsForDate(date)
    
    fun getTotalMinutesForSkill(skillId: Long): Flow<Int?> = skillDao.getTotalMinutesForSkill(skillId)
    
    fun getTotalPointsFromSkills(): Flow<Int?> = skillDao.getTotalPointsFromSkills()
    
    fun getTotalSkillMinutes(): Flow<Int?> = skillDao.getTotalSkillMinutes()
    
    suspend fun getSkillLogsCountForDate(date: LocalDate): Int = skillDao.getSkillLogsCountForDate(date)
    
    suspend fun addSkill(skill: Skill): Long = skillDao.insertSkill(skill)
    
    suspend fun updateSkill(skill: Skill) = skillDao.updateSkill(skill)
    
    suspend fun deleteSkill(skill: Skill) = skillDao.deleteSkill(skill)
    
    suspend fun logSkillTime(skillId: Long, date: LocalDate, minutes: Int, pointsEarned: Int): Long {
        skillDao.updateSkillTotalMinutes(skillId, minutes)
        return skillDao.insertSkillLog(
            SkillLog(
                skillId = skillId,
                date = date,
                minutes = minutes,
                pointsEarned = pointsEarned
            )
        )
    }
    
    suspend fun deleteSkillLog(skillLog: SkillLog) = skillDao.deleteSkillLog(skillLog)
}
