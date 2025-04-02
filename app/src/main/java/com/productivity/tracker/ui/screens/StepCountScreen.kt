package com.productivity.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.productivity.tracker.R
import com.productivity.tracker.data.database.entity.StepCount
import com.productivity.tracker.ui.theme.PointsColor
import com.productivity.tracker.util.DateUtils
import com.productivity.tracker.viewmodel.StepCountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCountScreen(
    viewModel: StepCountViewModel = hiltViewModel()
) {
    val todayStepCount by viewModel.todayStepCount.collectAsState()
    val recentStepCounts by viewModel.recentStepCounts.collectAsState()
    val totalSteps by viewModel.totalSteps.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddStepsDialog by remember { mutableStateOf(false) }
    var showSetGoalDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_steps)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Today's step counter card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Today's Steps",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = todayStepCount?.steps?.toString() ?: "0",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = if (todayStepCount?.goal ?: 1 > 0) {
                            (todayStepCount?.steps?.toFloat() ?: 0f) / (todayStepCount?.goal?.toFloat() ?: 1f)
                        } else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = stringResource(
                            R.string.steps_progress,
                            todayStepCount?.steps ?: 0,
                            todayStepCount?.goal ?: 10000
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { showAddStepsDialog = true }) {
                            Text(stringResource(R.string.step_add))
                        }
                        
                        OutlinedButton(onClick = { showSetGoalDialog = true }) {
                            Text(stringResource(R.string.step_goal))
                        }
                    }
                }
            }
            
            // Step count stats
            Text(
                text = "Total Steps: $totalSteps",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            // Recent step counts
            Text(
                text = "Recent Step Records",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            if (recentStepCounts.isEmpty()) {
                Text(
                    text = "No step records yet",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            } else {
                LazyColumn {
                    items(recentStepCounts) { stepCount ->
                        StepCountItem(stepCount = stepCount)
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
    
    // Add Steps Dialog
    if (showAddStepsDialog) {
        AddStepsDialog(
            currentGoal = todayStepCount?.goal ?: 10000,
            onDismiss = { showAddStepsDialog = false },
            onAddSteps = { steps ->
                viewModel.addSteps(steps, todayStepCount?.goal ?: 10000)
                showAddStepsDialog = false
            }
        )
    }
    
    // Set Goal Dialog
    if (showSetGoalDialog) {
        SetStepGoalDialog(
            currentGoal = todayStepCount?.goal ?: 10000,
            onDismiss = { showSetGoalDialog = false },
            onSetGoal = { goal ->
                viewModel.updateStepGoal(goal)
                showSetGoalDialog = false
            }
        )
    }
}

@Composable
fun StepCountItem(stepCount: StepCount) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = DateUtils.formatDate(stepCount.date),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "${stepCount.steps} / ${stepCount.goal} steps",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (stepCount.pointsEarned > 0) {
                Badge(
                    containerColor = PointsColor
                ) {
                    Text(
                        text = "+${stepCount.pointsEarned}",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddStepsDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onAddSteps: (Int) -> Unit
) {
    var steps by remember { mutableStateOf("") }
    var stepsError by remember { mutableStateOf(false) }
    
    fun validateSteps(): Boolean {
        val stepsValue = steps.toIntOrNull()
        stepsError = stepsValue == null || stepsValue <= 0
        return !stepsError
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
                    text = stringResource(R.string.step_add),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = steps,
                    onValueChange = { 
                        steps = it
                        validateSteps()
                    },
                    label = { Text(stringResource(R.string.step_count)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = stepsError
                )
                
                if (stepsError) {
                    Text(
                        text = stringResource(R.string.error_step_count),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                            if (validateSteps()) {
                                onAddSteps(steps.toInt())
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

@Composable
fun SetStepGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSetGoal: (Int) -> Unit
) {
    var goal by remember { mutableStateOf(currentGoal.toString()) }
    var goalError by remember { mutableStateOf(false) }
    
    fun validateGoal(): Boolean {
        val goalValue = goal.toIntOrNull()
        goalError = goalValue == null || goalValue <= 0
        return !goalError
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
                    text = stringResource(R.string.step_goal),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = goal,
                    onValueChange = { 
                        goal = it
                        validateGoal()
                    },
                    label = { Text("Daily Step Goal") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = goalError
                )
                
                if (goalError) {
                    Text(
                        text = stringResource(R.string.error_step_goal),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
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
                            if (validateGoal()) {
                                onSetGoal(goal.toInt())
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
