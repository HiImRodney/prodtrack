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
import com.productivity.tracker.data.database.entity.Task
import com.productivity.tracker.ui.components.TaskItem
import com.productivity.tracker.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val activeTasks by viewModel.activeTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showCompletedTasks by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_tasks)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Toggle for showing active/completed tasks
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Active")
                Switch(
                    checked = showCompletedTasks,
                    onCheckedChange = { showCompletedTasks = it }
                )
                Text(text = "Completed")
            }
            
            // Task list
            if (showCompletedTasks) {
                // Completed tasks
                if (completedTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No completed tasks yet",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(completedTasks) { task ->
                            TaskItem(
                                task = task,
                                onCompleteClick = { /* Already completed */ },
                                onDeleteClick = { viewModel.deleteTask(task) },
                                isCompleted = true
                            )
                        }
                    }
                }
            } else {
                // Active tasks
                if (activeTasks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.tasks_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(activeTasks) { task ->
                            TaskItem(
                                task = task,
                                onCompleteClick = { viewModel.completeTask(task) },
                                onDeleteClick = { viewModel.deleteTask(task) },
                                isCompleted = false
                            )
                        }
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
    
    // Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onTaskAdded = { title, description, duration ->
                viewModel.addTask(title, description, duration)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(30) } // Default to 30 minutes
    
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
                    text = stringResource(R.string.task_add),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Duration selection
                Text(
                    text = stringResource(R.string.task_duration),
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DurationOption(
                        text = stringResource(R.string.task_duration_15),
                        duration = 15,
                        isSelected = selectedDuration == 15,
                        onClick = { selectedDuration = 15 }
                    )
                    
                    DurationOption(
                        text = stringResource(R.string.task_duration_30),
                        duration = 30,
                        isSelected = selectedDuration == 30,
                        onClick = { selectedDuration = 30 }
                    )
                    
                    DurationOption(
                        text = stringResource(R.string.task_duration_60),
                        duration = 60,
                        isSelected = selectedDuration == 60,
                        onClick = { selectedDuration = 60 }
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
                        onClick = { onTaskAdded(title, description, selectedDuration) },
                        enabled = title.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun DurationOption(
    text: String,
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(text)
    }
}
