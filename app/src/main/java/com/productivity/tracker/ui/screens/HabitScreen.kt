package com.productivity.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.productivity.tracker.R
import com.productivity.tracker.ui.components.HabitItem
import com.productivity.tracker.viewmodel.HabitViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(
    viewModel: HabitViewModel = hiltViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    val habitLogs by viewModel.habitLogs.collectAsState()
    val progressMap by viewModel.progressMap.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddHabitDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_habits)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddHabitDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Habit list
            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.habits_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(habits) { habit ->
                        val logs = habitLogs[habit.id] ?: emptyList()
                        val progress = progressMap[habit.id] ?: 0
                        val isLoggedToday = logs.any { it.date == LocalDate.now() }
                        
                        HabitItem(
                            habit = habit,
                            progress = progress,
                            targetCount = habit.targetCount,
                            isLoggedToday = isLoggedToday,
                            onLogClick = { viewModel.logHabit(habit.id) },
                            onDeleteClick = { viewModel.deleteHabit(habit) }
                        )
                    }
                }
            }
            
            // Error message display
            errorMessage?.let {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(it)
                }
            }
        }
    }
    
    // Add Habit Dialog
    if (showAddHabitDialog) {
        AddHabitDialog(
            onDismiss = { showAddHabitDialog = false },
            onHabitAdded = { name, description, targetCount, periodWeeks ->
                viewModel.addHabit(name, description, targetCount, periodWeeks)
                showAddHabitDialog = false
            }
        )
    }
}

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onHabitAdded: (String, String, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var targetCount by remember { mutableStateOf("12") } // Default: 12 times
    var periodWeeks by remember { mutableStateOf("4") } // Default: 4 weeks
    
    var targetCountError by remember { mutableStateOf(false) }
    var periodWeeksError by remember { mutableStateOf(false) }
    
    fun validateInputs(): Boolean {
        targetCountError = targetCount.toIntOrNull()?.let { it <= 0 } ?: true
        periodWeeksError = periodWeeks.toIntOrNull()?.let { it <= 0 } ?: true
        
        return !targetCountError && !periodWeeksError && name.isNotBlank()
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.habit_add),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.habit_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank()
                )
                
                if (name.isBlank()) {
                    Text(
                        text = stringResource(R.string.error_habit_name),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.habit_description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Target count input
                OutlinedTextField(
                    value = targetCount,
                    onValueChange = { 
                        targetCount = it
                        targetCountError = it.toIntOrNull()?.let { num -> num <= 0 } ?: true
                    },
                    label = { Text(stringResource(R.string.habit_goal)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = targetCountError
                )
                
                if (targetCountError) {
                    Text(
                        text = stringResource(R.string.error_habit_goal),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Period weeks input
                OutlinedTextField(
                    value = periodWeeks,
                    onValueChange = { 
                        periodWeeks = it
                        periodWeeksError = it.toIntOrNull()?.let { num -> num <= 0 } ?: true
                    },
                    label = { Text(stringResource(R.string.habit_period)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = periodWeeksError
                )
                
                if (periodWeeksError) {
                    Text(
                        text = "Please enter a valid period in weeks",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (validateInputs()) {
                                onHabitAdded(
                                    name,
                                    description,
                                    targetCount.toInt(),
                                    periodWeeks.toInt()
                                )
                            }
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
