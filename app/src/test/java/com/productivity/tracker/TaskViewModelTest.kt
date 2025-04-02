package com.productivity.tracker

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.productivity.tracker.data.database.entity.Task
import com.productivity.tracker.data.repository.TaskRepository
import com.productivity.tracker.util.PointsCalculator
import com.productivity.tracker.viewmodel.TaskViewModel
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
import java.time.LocalDateTime

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TaskViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    @Mock
    private lateinit var taskRepository: TaskRepository
    
    @Mock
    private lateinit var pointsCalculator: PointsCalculator
    
    private lateinit var viewModel: TaskViewModel
    
    private val activeTasks = listOf(
        Task(id = 1, title = "Test Task 1", durationMinutes = 15),
        Task(id = 2, title = "Test Task 2", durationMinutes = 30)
    )
    
    private val completedTasks = listOf(
        Task(
            id = 3, 
            title = "Test Task 3", 
            durationMinutes = 60, 
            isCompleted = true, 
            completedAt = LocalDateTime.now(), 
            pointsEarned = 20
        )
    )
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        `when`(taskRepository.getActiveTasks()).thenReturn(flowOf(activeTasks))
        `when`(taskRepository.getCompletedTasks()).thenReturn(flowOf(completedTasks))
        
        viewModel = TaskViewModel(taskRepository, pointsCalculator)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state loads active and completed tasks`() = runTest {
        // Initialize the viewModel to trigger repository calls
        
        // Advance the dispatcher to allow flows to be collected
        testScheduler.advanceUntilIdle()
        
        // Verify
        verify(taskRepository).getActiveTasks()
        verify(taskRepository).getCompletedTasks()
        
        assert(viewModel.activeTasks.value == activeTasks)
        assert(viewModel.completedTasks.value == completedTasks)
    }
    
    @Test
    fun `add task with valid data calls repository`() = runTest {
        // When
        viewModel.addTask("New Task", "Description", 30)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(taskRepository).addTask(
            argThat { task ->
                task.title == "New Task" && 
                task.description == "Description" && 
                task.durationMinutes == 30
            }
        )
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `add task with empty title sets error message`() = runTest {
        // When
        viewModel.addTask("", "Description", 30)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(taskRepository, never()).addTask(any())
        assert(viewModel.errorMessage.value != null)
        assert(viewModel.errorMessage.value!!.contains("title"))
    }
    
    @Test
    fun `add task with invalid duration sets error message`() = runTest {
        // When
        viewModel.addTask("Task", "Description", 45)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(taskRepository, never()).addTask(any())
        assert(viewModel.errorMessage.value != null)
        assert(viewModel.errorMessage.value!!.contains("duration"))
    }
    
    @Test
    fun `complete task calculates points and calls repository`() = runTest {
        // Given
        val task = activeTasks[0]
        `when`(pointsCalculator.calculateTaskPoints(task.durationMinutes)).thenReturn(5)
        
        // When
        viewModel.completeTask(task)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(pointsCalculator).calculateTaskPoints(task.durationMinutes)
        verify(taskRepository).completeTask(eq(task), eq(5))
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `delete task calls repository`() = runTest {
        // Given
        val task = activeTasks[0]
        
        // When
        viewModel.deleteTask(task)
        testScheduler.advanceUntilIdle()
        
        // Then
        verify(taskRepository).deleteTask(task)
        assert(viewModel.errorMessage.value == null)
    }
    
    @Test
    fun `clear error clears error message`() = runTest {
        // Given - set an error first
        viewModel.addTask("", "", 30)
        testScheduler.advanceUntilIdle()
        assert(viewModel.errorMessage.value != null)
        
        // When
        viewModel.clearError()
        testScheduler.advanceUntilIdle()
        
        // Then
        assert(viewModel.errorMessage.value == null)
    }
}
