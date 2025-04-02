package com.productivity.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.productivity.tracker.R
import com.productivity.tracker.data.database.entity.Habit
import com.productivity.tracker.ui.theme.HabitCardDark
import com.productivity.tracker.ui.theme.HabitCardLight
import com.productivity.tracker.ui.theme.PointsColor

@Composable
fun HabitItem(
    habit: Habit,
    progress: Int,
    targetCount: Int,
    isLoggedToday: Boolean,
    onLogClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cardColor = if (isSystemInDarkTheme()) HabitCardDark else HabitCardLight
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Habit name and goal row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "Goal: ${habit.targetCount}/${habit.periodWeeks}w",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Description if present
            if (habit.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = habit.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Progress indicator
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stringResource(R.string.habit_progress, progress, targetCount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = if (targetCount > 0) {
                    progress.toFloat() / targetCount.toFloat()
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            
            // Action buttons
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onLogClick,
                    enabled = !isLoggedToday,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLoggedToday) PointsColor else MaterialTheme.colorScheme.primary,
                        disabledContainerColor = PointsColor.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoggedToday) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Already Logged"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Logged Today")
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Habit"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.habit_log))
                    }
                }
                
                OutlinedButton(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Habit"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}
