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
import com.productivity.tracker.data.database.entity.Skill
import com.productivity.tracker.data.database.entity.SkillLog
import com.productivity.tracker.ui.components.SkillItem
import com.productivity.tracker.util.DateUtils
import com.productivity.tracker.viewmodel.SkillViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillScreen(
    viewModel: SkillViewModel = hiltViewModel()
) {
    val skills by viewModel.skills.collectAsState()
    val skillLogs by viewModel.skillLogs.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }
    var showLogTimeDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_skills)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSkillDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Skill")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Skill list
            if (skills.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.skills_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(skills) { skill ->
                        val logs = skillLogs[skill.id] ?: emptyList()
                        val totalTime = skill.totalMinutes
                        
                        SkillItem(
                            skill = skill,
                            totalTimeFormatted = DateUtils.minutesToReadableFormat(totalTime),
                            onLogTimeClick = {
                                selectedSkill = skill
                                showLogTimeDialog = true
                            },
                            onDeleteClick = { viewModel.deleteSkill(skill) }
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
    
    // Add Skill Dialog
    if (showAddSkillDialog) {
        AddSkillDialog(
            onDismiss = { showAddSkillDialog = false },
            onSkillAdded = { name, description ->
                viewModel.addSkill(name, description)
                showAddSkillDialog = false
            }
        )
    }
    
    // Log Skill Time Dialog
    if (showLogTimeDialog && selectedSkill != null) {
        LogSkillTimeDialog(
            skill = selectedSkill!!,
            onDismiss = { 
                showLogTimeDialog = false
                selectedSkill = null
            },
            onTimeLogged = { skillId, minutes ->
                viewModel.logSkillTime(skillId, minutes)
                showLogTimeDialog = false
                selectedSkill = null
            }
        )
    }
}

@Composable
fun AddSkillDialog(
    onDismiss: () -> Unit,
    onSkillAdded: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
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
                    text = stringResource(R.string.skill_add),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.skill_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank()
                )
                
                if (name.isBlank()) {
                    Text(
                        text = stringResource(R.string.error_skill_name),
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
                    label = { Text(stringResource(R.string.skill_description)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
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
                        onClick = { onSkillAdded(name, description) },
                        enabled = name.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun LogSkillTimeDialog(
    skill: Skill,
    onDismiss: () -> Unit,
    onTimeLogged: (Long, Int) -> Unit
) {
    var minutes by remember { mutableStateOf("") }
    var minutesError by remember { mutableStateOf(false) }
    
    fun validateMinutes(): Boolean {
        val minutesValue = minutes.toIntOrNull()
        minutesError = minutesValue == null || minutesValue <= 0
        return !minutesError
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
                    text = stringResource(R.string.skill_log_time),
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Minutes input
                OutlinedTextField(
                    value = minutes,
                    onValueChange = { 
                        minutes = it
                        validateMinutes()
                    },
                    label = { Text(stringResource(R.string.skill_minutes)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = minutesError
                )
                
                if (minutesError) {
                    Text(
                        text = "Please enter a valid number of minutes",
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
                            if (validateMinutes()) {
                                onTimeLogged(skill.id, minutes.toInt())
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
