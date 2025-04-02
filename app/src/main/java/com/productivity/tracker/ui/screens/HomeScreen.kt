package com.productivity.tracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.productivity.tracker.R
import com.productivity.tracker.ui.components.NidgeCardDisplay
import com.productivity.tracker.ui.components.PointsCard
import com.productivity.tracker.ui.components.StreakDisplay
import com.productivity.tracker.viewmodel.StatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val totalPoints by viewModel.totalPoints.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val canUseNidgeCard by viewModel.canUseNidgeCardToday.collectAsState()
    val nidgeCardUsed by viewModel.nidgeCardUsedThisWeek.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) }
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
            // Welcome message
            Text(
                text = "Welcome to Your Productivity Tracker",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Points card
            PointsCard(totalPoints = totalPoints)
            
            // Streak display
            StreakDisplay(
                currentStreak = currentStreak,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Nidge Card
            NidgeCardDisplay(
                canUseNidgeCard = canUseNidgeCard,
                nidgeCardUsed = nidgeCardUsed,
                onUseNidgeCard = { viewModel.useNidgeCard() }
            )
            
            // Quick access buttons
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Start
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* Navigation handled by bottom nav */ },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(stringResource(R.string.nav_tasks))
                }
                
                OutlinedButton(
                    onClick = { /* Navigation handled by bottom nav */ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(stringResource(R.string.nav_habits))
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* Navigation handled by bottom nav */ },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(stringResource(R.string.nav_skills))
                }
                
                OutlinedButton(
                    onClick = { /* Navigation handled by bottom nav */ },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text(stringResource(R.string.nav_steps))
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
            }
        }
    }
}
