package com.productivity.tracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.productivity.tracker.data.database.entity.Habit
import com.productivity.tracker.data.database.entity.HabitLog
import com.productivity.tracker.data.repository.HabitRepository
import com.productivity.tracker.util.PointsCalculator
import com.productivity.tracker.viewmodel.HabitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HabitViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    @Mock
    private lateinit var habitRepository: HabitRepository
    
    @Mock
    private lateinit var pointsCalculator: PointsCalculator
    
    private lateinit var viewModel: HabitViewModel
    
    private val testHabits = listOf(
        Habit(id = 1, name = "Gym", targetCount = 12, periodWeeks = 4),
        Habit(id = 2, name = "Meditation", targetCount = 7, periodWeeks = 1)
    )
    
    private val habitLogs = mapOf(
        1L to listOf(
            HabitLog(habitId = 1, date = LocalDate.now().minusDays(1), pointsEarned = 15),
            HabitLog(habitId = 1, date = LocalDate.now().minusDays(3), pointsEarned = 15)
        ),
        2L to listOf(
            HabitLog(habitId = 2, date = LocalDate.now(), pointsEarned = 15)
        )
    )
    
    private val progressMap = mapOf(
        1L to 2,
        2L to 1
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        `when`(habitRepository.getAllHabits()).thenReturn(flowOf(testHabits))
        
        testHabits.forEach { habit ->
            val logs = habitLogs[habit.id] ?: emptyList()
            `when`(habitRepository.getHabitLogsForHabit(habit.id)).thenReturn(flowOf(logs))
            
            val progress = progressMap[habit.id] ?: 0
            val today = LocalDate.now()
            val period = today.minusWeeks(habit.periodWeeks.toLong())
            `when`(habitRepository.getHabitCompletionCountForPeriod(habit.id, period, today))
                .thenReturn(flowOf(progress))
        }
        
        `when`(pointsCalculator.calculateHabitPoints()).thenReturn(15)
        
        viewModel = HabitViewModel(habitRepository, pointsCalculator)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state loads habits and their logs`() = runTest {
        // Initialize the viewModel to trigger repository calls
        
        // Advance the dispatcher to allow flows to be collected
        testScheduler.advanceUntilIdle()
        
        // Verify
        verify(habitRepository).getAllHabits()
        
        testHabits.forEach { habit ->
            verify(habitRepository).getHabitLogsForHabit(habit.id)
        }
        
        assert(viewModel.habits.value == testHabits)
        
        // Check that habit logs were loaded - get first habit logs
        val firstHabitLogs = viewModel.habitLogs.value[1L]
        assert(firstHabitLogs == habitLogs[1L])
    }
    
    @Test
    fun `add habit with valid data calls repository`() = runTest {
        // When
        viewModel.addHabit("Running", "Daily run", 20, 4)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(habitRepository).addHabit(
            argThat { habit ->
                habit.name == "Running" && 
                habit.description == "Daily run" && 
                habit.targetCount == 20 &&
                habit.periodWeeks == 4
            }
        )
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `add habit with empty name sets error message`() = runTest {
        // When
        viewModel.addHabit("", "Description", 20, 4)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(habitRepository, never()).addHabit(any())
        assert(viewModel.errorMessage.value != null)
        assert(viewModel.errorMessage.value!!.contains("name"))
    }
    
    @Test
    fun `add habit with invalid target count sets error message`() = runTest {
        // When
        viewModel.addHabit("Habit", "Description", 0, 4)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(habitRepository, never()).addHabit(any())
        assert(viewModel.errorMessage.value != null)
        assert(viewModel.errorMessage.value!!.contains("count"))
    }
    
    @Test
    fun `add habit with invalid period weeks sets error message`() = runTest {
        // When
        viewModel.addHabit("Habit", "Description", 12, 0)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(habitRepository, never()).addHabit(any())
        assert(viewModel.errorMessage.value != null)
        assert(viewModel.errorMessage.value!!.contains("weeks"))
    }
    
    @Test
    fun `log habit calculates points and calls repository`() = runTest {
        // Given
        val habitId = 1L
        val today = LocalDate.now()
        
        // When
        viewModel.logHabit(habitId, today)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(pointsCalculator).calculateHabitPoints()
        verify(habitRepository).logHabit(eq(habitId), eq(today), eq(15))
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `delete habit log calls repository`() = runTest {
        // Given
        val habitId = 1L
        val date = LocalDate.now()
        
        // When
        viewModel.deleteHabitLog(habitId, date)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(habitRepository).deleteHabitLog(habitId, date)
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `delete habit calls repository`() = runTest {
        // Given
        val habit = testHabits[0]
        
        // When
        viewModel.deleteHabit(habit)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(habitRepository).deleteHabit(habit)
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `clear error clears error message`() = runTest {
        // Given - set an error first
        viewModel.addHabit("", "", 0, 0)
        testScheduler.advanceUntilIdle()
        assert(viewModel.errorMessage.value != null)
        
        // When
        viewModel.clearError()
        testScheduler.advanceUntilIdle()
        
        // Then
        assert(viewModel.errorMessage.value == null)
    }
}
