package com.productivity.tracker.util

import java.time.LocalDate
import javax.inject.Inject

/**
 * Utility class to track and calculate streaks
 */
class StreakTracker @Inject constructor() {

    /**
     * Calculate the current streak based on activity history and nidge card usage
     *
     * @param activityDates List of dates on which user had any activity
     * @param nidgeCardUsageDates List of dates on which user used a nidge card
     * @return Current streak count in days
     */
    fun calculateCurrentStreak(
        activityDates: List<LocalDate>,
        nidgeCardUsageDates: List<LocalDate>
    ): Int {
        if (activityDates.isEmpty()) return 0
        
        val sortedActivityDates = activityDates.sorted()
        val today = LocalDate.now()
        
        // Check if there's activity today
        val hasActivityToday = sortedActivityDates.contains(today)
        // Check if there's activity yesterday
        val hasActivityYesterday = sortedActivityDates.contains(today.minusDays(1))
        // Check if nidge card was used yesterday
        val usedNidgeCardYesterday = nidgeCardUsageDates.contains(today.minusDays(1))
        
        // If no activity today and no activity/nidge yesterday, streak is broken
        if (!hasActivityToday && !hasActivityYesterday && !usedNidgeCardYesterday) {
            return 0
        }
        
        var streakCount = if (hasActivityToday) 1 else 0
        var currentDate = today.minusDays(1)
        
        // Count backwards from yesterday until streak is broken
        while (true) {
            val hasActivityOnDate = sortedActivityDates.contains(currentDate)
            val usedNidgeCardOnDate = nidgeCardUsageDates.contains(currentDate)
            
            if (hasActivityOnDate || usedNidgeCardOnDate) {
                if (hasActivityOnDate) {
                    streakCount++
                }
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }
        
        return streakCount
    }
    
    /**
     * Determines if a nidge card can be used today based on:
     * 1. User hasn't used a nidge card this week
     * 2. User had activity yesterday (streak is active)
     *
     * @param activityDates List of dates with activity
     * @param nidgeCardUsageDates List of dates when nidge cards were used
     * @return True if nidge card can be used today
     */
    fun canUseNidgeCardToday(
        activityDates: List<LocalDate>,
        nidgeCardUsageDates: List<LocalDate>
    ): Boolean {
        val today = LocalDate.now()
        val startOfWeek = DateUtils.getStartOfWeek(today)
        val endOfWeek = DateUtils.getEndOfWeek(today)
        
        // Check if nidge card was already used this week
        val usedNidgeThisWeek = nidgeCardUsageDates.any { date -> 
            date >= startOfWeek && date <= endOfWeek 
        }
        
        if (usedNidgeThisWeek) {
            return false
        }
        
        // Check if there was activity yesterday (streak is active)
        val hasActivityYesterday = activityDates.contains(today.minusDays(1))
        
        return hasActivityYesterday
    }
    
    /**
     * Determines if the user had any activity today
     *
     * @param taskCount Number of tasks completed today
     * @param habitCount Number of habits logged today
     * @param skillCount Number of skill sessions logged today
     * @param stepGoalMet Whether the step goal was met today
     * @return True if user had any activity today
     */
    fun hasActivityToday(
        taskCount: Int,
        habitCount: Int,
        skillCount: Int,
        stepGoalMet: Boolean
    ): Boolean {
        return taskCount > 0 || habitCount > 0 || skillCount > 0 || stepGoalMet
    }
}
