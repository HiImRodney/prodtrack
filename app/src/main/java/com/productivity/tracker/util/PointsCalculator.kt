package com.productivity.tracker.util

import javax.inject.Inject

/**
 * Calculator for determining points earned for different activities
 */
class PointsCalculator @Inject constructor() {

    companion object {
        // Points for task completion based on duration
        private const val POINTS_TASK_15_MIN = 5
        private const val POINTS_TASK_30_MIN = 10
        private const val POINTS_TASK_60_MIN = 20
        
        // Points for habit completion
        private const val POINTS_HABIT_COMPLETION = 15
        
        // Points for skill learning - per 15 minutes
        private const val POINTS_SKILL_PER_15_MIN = 5
        
        // Points for reaching step goal
        private const val POINTS_STEP_GOAL_REACHED = 10
        
        // Streak bonus multipliers
        private const val STREAK_BONUS_3_DAYS = 1.1 // 10% bonus
        private const val STREAK_BONUS_7_DAYS = 1.25 // 25% bonus
        private const val STREAK_BONUS_14_DAYS = 1.5 // 50% bonus
        private const val STREAK_BONUS_30_DAYS = 2.0 // 100% bonus
    }
    
    /**
     * Calculate points for completing a task based on its duration
     */
    fun calculateTaskPoints(durationMinutes: Int): Int {
        return when (durationMinutes) {
            15 -> POINTS_TASK_15_MIN
            30 -> POINTS_TASK_30_MIN
            60 -> POINTS_TASK_60_MIN
            else -> durationMinutes / 3 // Fallback calculation
        }
    }
    
    /**
     * Calculate points for completing a habit
     */
    fun calculateHabitPoints(): Int {
        return POINTS_HABIT_COMPLETION
    }
    
    /**
     * Calculate points for logging skill learning time
     */
    fun calculateSkillPoints(minutes: Int): Int {
        return (minutes / 15) * POINTS_SKILL_PER_15_MIN
    }
    
    /**
     * Calculate points for reaching daily step goal
     */
    fun calculateStepGoalPoints(): Int {
        return POINTS_STEP_GOAL_REACHED
    }
    
    /**
     * Apply streak bonus to points, if applicable
     */
    fun applyStreakBonus(points: Int, streakDays: Int): Int {
        val multiplier = when {
            streakDays >= 30 -> STREAK_BONUS_30_DAYS
            streakDays >= 14 -> STREAK_BONUS_14_DAYS
            streakDays >= 7 -> STREAK_BONUS_7_DAYS
            streakDays >= 3 -> STREAK_BONUS_3_DAYS
            else -> 1.0
        }
        
        return (points * multiplier).toInt()
    }
}
