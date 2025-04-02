package com.productivity.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.productivity.tracker.R
import com.productivity.tracker.ui.theme.StreakGold

@Composable
fun StreakDisplay(
    currentStreak: Int,
    longestStreak: Int = currentStreak,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (currentStreak > 0) StreakGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current streak count with fire icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (currentStreak > 0) {
                    Text(
                        text = "🔥",  // fire emoji
                        style = MaterialTheme.typography.headlineMedium
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Text(
                    text = stringResource(R.string.stats_streak, currentStreak),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (currentStreak > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // If there's a longest streak different from current, show it
            if (longestStreak > currentStreak) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stringResource(R.string.stats_longest_streak, longestStreak),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Streak bonus indicators
            if (currentStreak >= 3) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StreakBonusIndicator(
                        days = 3,
                        bonus = "+10%",
                        isActive = currentStreak >= 3
                    )
                    
                    StreakBonusIndicator(
                        days = 7,
                        bonus = "+25%",
                        isActive = currentStreak >= 7
                    )
                    
                    StreakBonusIndicator(
                        days = 14,
                        bonus = "+50%",
                        isActive = currentStreak >= 14
                    )
                    
                    StreakBonusIndicator(
                        days = 30,
                        bonus = "+100%",
                        isActive = currentStreak >= 30
                    )
                }
            }
        }
    }
}

@Composable
fun StreakBonusIndicator(
    days: Int,
    bonus: String,
    isActive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) StreakGold else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = days.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isActive) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = bonus,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
