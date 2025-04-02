package com.productivity.tracker

import com.productivity.tracker.util.PointsCalculator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PointsCalculatorTest {
    
    private lateinit var pointsCalculator: PointsCalculator
    
    @Before
    fun setup() {
        pointsCalculator = PointsCalculator()
    }
    
    @Test
    fun `calculateTaskPoints returns correct points for 15 minute task`() {
        // When
        val points = pointsCalculator.calculateTaskPoints(15)
        
        // Then
        assertEquals(5, points)
    }
    
    @Test
    fun `calculateTaskPoints returns correct points for 30 minute task`() {
        // When
        val points = pointsCalculator.calculateTaskPoints(30)
        
        // Then
        assertEquals(10, points)
    }
    
    @Test
    fun `calculateTaskPoints returns correct points for 60 minute task`() {
        // When
        val points = pointsCalculator.calculateTaskPoints(60)
        
        // Then
        assertEquals(20, points)
    }
    
    @Test
    fun `calculateTaskPoints handles custom duration`() {
        // When
        val points = pointsCalculator.calculateTaskPoints(45)
        
        // Then
        assertEquals(45 / 3, points) // Fallback calculation is duration / 3
    }
    
    @Test
    fun `calculateHabitPoints returns correct fixed value`() {
        // When
        val points = pointsCalculator.calculateHabitPoints()
        
        // Then
        assertEquals(15, points)
    }
    
    @Test
    fun `calculateSkillPoints returns correct points for 15 minutes`() {
        // When
        val points = pointsCalculator.calculateSkillPoints(15)
        
        // Then
        assertEquals(5, points)
    }
    
    @Test
    fun `calculateSkillPoints returns correct points for 30 minutes`() {
        // When
        val points = pointsCalculator.calculateSkillPoints(30)
        
        // Then
        assertEquals(10, points) // 2 * 5
    }
    
    @Test
    fun `calculateSkillPoints returns correct points for 60 minutes`() {
        // When
        val points = pointsCalculator.calculateSkillPoints(60)
        
        // Then
        assertEquals(20, points) // 4 * 5
    }
    
    @Test
    fun `calculateSkillPoints rounds down for partial 15 minute blocks`() {
        // When
        val points = pointsCalculator.calculateSkillPoints(22)
        
        // Then
        assertEquals(5, points) // Only one 15-minute block
    }
    
    @Test
    fun `calculateStepGoalPoints returns correct fixed value`() {
        // When
        val points = pointsCalculator.calculateStepGoalPoints()
        
        // Then
        assertEquals(10, points)
    }
    
    @Test
    fun `applyStreakBonus with no streak applies no bonus`() {
        // When
        val basePoints = 10
        val result = pointsCalculator.applyStreakBonus(basePoints, 0)
        
        // Then
        assertEquals(10, result)
    }
    
    @Test
    fun `applyStreakBonus with 3 day streak applies 10 percent bonus`() {
        // When
        val basePoints = 10
        val result = pointsCalculator.applyStreakBonus(basePoints, 3)
        
        // Then
        assertEquals(11, result) // 10 + 10% = 11
    }
    
    @Test
    fun `applyStreakBonus with 7 day streak applies 25 percent bonus`() {
        // When
        val basePoints = 20
        val result = pointsCalculator.applyStreakBonus(basePoints, 7)
        
        // Then
        assertEquals(25, result) // 20 + 25% = 25
    }
    
    @Test
    fun `applyStreakBonus with 14 day streak applies 50 percent bonus`() {
        // When
        val basePoints = 20
        val result = pointsCalculator.applyStreakBonus(basePoints, 14)
        
        // Then
        assertEquals(30, result) // 20 + 50% = 30
    }
    
    @Test
    fun `applyStreakBonus with 30 day streak applies 100 percent bonus`() {
        // When
        val basePoints = 25
        val result = pointsCalculator.applyStreakBonus(basePoints, 30)
        
        // Then
        assertEquals(50, result) // 25 + 100% = 50
    }
}
