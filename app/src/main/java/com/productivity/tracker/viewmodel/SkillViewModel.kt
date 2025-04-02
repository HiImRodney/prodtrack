package com.productivity.tracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.productivity.tracker.data.database.entity.Skill
import com.productivity.tracker.data.database.entity.SkillLog
import com.productivity.tracker.data.repository.SkillRepository
import com.productivity.tracker.util.PointsCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SkillViewModel @Inject constructor(
    private val skillRepository: SkillRepository,
    private val pointsCalculator: PointsCalculator
) : ViewModel() {

    private val _skills = MutableStateFlow<List<Skill>>(emptyList())
    val skills: StateFlow<List<Skill>> = _skills.asStateFlow()

    private val _skillLogs = MutableStateFlow<Map<Long, List<SkillLog>>>(emptyMap())
    val skillLogs: StateFlow<Map<Long, List<SkillLog>>> = _skillLogs.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadSkills()
    }

    private fun loadSkills() {
        viewModelScope.launch {
            try {
                skillRepository.getAllSkills().collect { skillsList ->
                    _skills.value = skillsList
                    updateSkillLogs(skillsList)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load skills: ${e.message}"
            }
        }
    }

    private fun updateSkillLogs(skills: List<Skill>) {
        skills.forEach { skill ->
            viewModelScope.launch {
                try {
                    skillRepository.getSkillLogsForSkill(skill.id).collect { logs ->
                        _skillLogs.update { currentLogs ->
                            val mutableLogs = currentLogs.toMutableMap()
                            mutableLogs[skill.id] = logs
                            mutableLogs
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to load skill logs: ${e.message}"
                }
            }
        }
    }

    fun addSkill(name: String, description: String) {
        if (name.isBlank()) {
            _errorMessage.value = "Skill name cannot be empty"
            return
        }

        viewModelScope.launch {
            try {
                val skill = Skill(
                    name = name,
                    description = description
                )
                skillRepository.addSkill(skill)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add skill: ${e.message}"
            }
        }
    }

    fun logSkillTime(skillId: Long, minutes: Int, date: LocalDate = LocalDate.now()) {
        if (minutes <= 0) {
            _errorMessage.value = "Minutes must be greater than 0"
            return
        }

        viewModelScope.launch {
            try {
                val points = pointsCalculator.calculateSkillPoints(minutes)
                skillRepository.logSkillTime(skillId, date, minutes, points)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to log skill time: ${e.message}"
            }
        }
    }

    fun deleteSkillLog(skillLog: SkillLog) {
        viewModelScope.launch {
            try {
                skillRepository.deleteSkillLog(skillLog)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete skill log: ${e.message}"
            }
        }
    }

    fun deleteSkill(skill: Skill) {
        viewModelScope.launch {
            try {
                skillRepository.deleteSkill(skill)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete skill: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
