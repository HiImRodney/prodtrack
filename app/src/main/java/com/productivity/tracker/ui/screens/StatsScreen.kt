package com.productivity.tracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.productivity.tracker.R
import com.productivity.tracker.ui.components.NidgeCardDisplay
import com.productivity.tracker.ui.components.StreakDisplay
import com.productivity.tracker.ui.theme.PointsColor
import com.productivity.tracker.util.DateUtils
import com.productivity.tracker.viewmodel.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val totalPoints by viewModel.totalPoints.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val longestStreak by viewModel.longestStreak.collectAsState()
    val tasksCompleted by viewModel.tasksCompleted.collectAsState()
    val habitsCompleted by viewModel.habitsCompleted.collectAsState()
    val totalSkillMinutes by viewModel.totalSkillMinutes.collectAsState()
    val totalSteps by viewModel.totalSteps.collectAsState()
    val hasActivityToday by viewModel.hasActivityToday.collectAsState()
    val canUseNidgeCard by viewModel.canUseNidgeCardToday.collectAsState()
    val nidgeCardUsedThisWeek by viewModel.nidgeCardUsedThisWeek.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    // Update streak when entering the screen
    LaunchedEffect(key1 = hasActivityToday) {
        if (hasActivityToday) {
            viewModel.updateStreakForToday()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_stats)) },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Stats"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Total points card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.stats_points, totalPoints),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Streak info
            StreakDisplay(
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Nidge Card
            NidgeCardDisplay(
                canUseNidgeCard = canUseNidgeCard,
                nidgeCardUsed = nidgeCardUsedThisWeek,
                onUseNidgeCard = { viewModel.useNidgeCard() }
            )
            
            // Activity totals
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Your Activity Totals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    StatRow(
                        label = "Tasks Completed",
                        value = tasksCompleted.toString()
                    )
                    
                    StatRow(
                        label = "Habits Completed",
                        value = habitsCompleted.toString()
                    )
                    
                    StatRow(
                        label = "Skill Learning Time",
                        value = DateUtils.minutesToReadableFormat(totalSkillMinutes)
                    )
                    
                    StatRow(
                        label = "Total Steps",
                        value = totalSteps.toString()
                    )
                }
            }
            
            // Activity status for today
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Today's Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Activity logged today",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (hasActivityToday) PointsColor else MaterialTheme.colorScheme.error
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hasActivityToday) {
                                Text(
                                    text = "✓",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "✗",
                                    color = MaterialTheme.colorScheme.onError,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nidge Card used this week",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (nidgeCardUsedThisWeek) PointsColor else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (nidgeCardUsedThisWeek) {
                                Text(
                                    text = "✓",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            } else {
                                Text(
                                    text = "✗",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
            
            // Error message display
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                Button(
                    onClick = { viewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text("Dismiss Error")
                }
            }
        }
    }
}

@Composable
fun StatRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
