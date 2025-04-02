package com.productivity.tracker

import com.productivity.tracker.util.StreakTracker
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class StreakTrackerTest {
    
    private lateinit var streakTracker: StreakTracker
    private val today = LocalDate.now()
    
    @Before
    fun setup() {
        streakTracker = StreakTracker()
    }
    
    @Test
    fun `calculateCurrentStreak returns 0 for empty activity`() {
        // Given
        val emptyActivity = emptyList<LocalDate>()
        val emptyNidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.calculateCurrentStreak(emptyActivity, emptyNidgeUsage)
        
        // Then
        assertEquals(0, result)
    }
    
    @Test
    fun `calculateCurrentStreak returns 1 for activity today only`() {
        // Given
        val activityDates = listOf(today)
        val emptyNidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.calculateCurrentStreak(activityDates, emptyNidgeUsage)
        
        // Then
        assertEquals(1, result)
    }
    
    @Test
    fun `calculateCurrentStreak returns 2 for activity today and yesterday`() {
        // Given
        val activityDates = listOf(today, today.minusDays(1))
        val emptyNidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.calculateCurrentStreak(activityDates, emptyNidgeUsage)
        
        // Then
        assertEquals(2, result)
    }
    
    @Test
    fun `calculateCurrentStreak returns correct streak for consecutive days`() {
        // Given
        val activityDates = listOf(
            today,
            today.minusDays(1),
            today.minusDays(2),
            today.minusDays(3),
            today.minusDays(4)
        )
        val emptyNidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.calculateCurrentStreak(activityDates, emptyNidgeUsage)
        
        // Then
        assertEquals(5, result)
    }
    
    @Test
    fun `calculateCurrentStreak breaks streak on missed day`() {
        // Given
        // Missing day at today.minusDays(2)
        val activityDates = listOf(
            today,
            today.minusDays(1),
            today.minusDays(3),
            today.minusDays(4)
        )
        val emptyNidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.calculateCurrentStreak(activityDates, emptyNidgeUsage)
        
        // Then
        assertEquals(2, result) // Only today and yesterday count
    }
    
    @Test
    fun `calculateCurrentStreak maintains streak with nidge card`() {
        // Given
        // Activity on today, but missing yesterday, nidge used yesterday
        val activityDates = listOf(
            today,
            today.minusDays(2),
            today.minusDays(3)
        )
        val nidgeUsage = listOf(today.minusDays(1))
        
        // When
        val result = streakTracker.calculateCurrentStreak(activityDates, nidgeUsage)
        
        // Then
        assertEquals(4, result) // Today + 3 previous days (including nidge)
    }
    
    @Test
    fun `calculateCurrentStreak returns 0 if no recent activity`() {
        // Given
        // Activity only 5 days ago
        val activityDates = listOf(today.minusDays(5))
        val emptyNidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.calculateCurrentStreak(activityDates, emptyNidgeUsage)
        
        // Then
        assertEquals(0, result) // Streak is broken
    }
    
    @Test
    fun `canUseNidgeCardToday returns false if used this week`() {
        // Given
        val activityDates = listOf(today.minusDays(1))
        val nidgeUsage = listOf(today.minusDays(3)) // Used earlier this week
        
        // When
        val result = streakTracker.canUseNidgeCardToday(activityDates, nidgeUsage)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `canUseNidgeCardToday returns true with active streak and no usage this week`() {
        // Given
        val activityDates = listOf(today.minusDays(1)) // Activity yesterday
        val nidgeUsage = listOf(today.minusDays(10)) // Used long ago
        
        // When
        val result = streakTracker.canUseNidgeCardToday(activityDates, nidgeUsage)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `canUseNidgeCardToday returns false with no active streak`() {
        // Given
        val activityDates = listOf(today.minusDays(2)) // No activity yesterday
        val nidgeUsage = emptyList<LocalDate>()
        
        // When
        val result = streakTracker.canUseNidgeCardToday(activityDates, nidgeUsage)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hasActivityToday returns true when any activity exists`() {
        // Given
        val taskCount = 1
        val habitCount = 0
        val skillCount = 0
        val stepGoalMet = false
        
        // When
        val result = streakTracker.hasActivityToday(taskCount, habitCount, skillCount, stepGoalMet)
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `hasActivityToday returns false when no activity exists`() {
        // Given
        val taskCount = 0
        val habitCount = 0
        val skillCount = 0
        val stepGoalMet = false
        
        // When
        val result = streakTracker.hasActivityToday(taskCount, habitCount, skillCount, stepGoalMet)
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `hasActivityToday returns true when step goal is met`() {
        // Given
        val taskCount = 0
        val habitCount = 0
        val skillCount = 0
        val stepGoalMet = true
        
        // When
        val result = streakTracker.hasActivityToday(taskCount, habitCount, skillCount, stepGoalMet)
        
        // Then
        assertTrue(result)
    }
}
