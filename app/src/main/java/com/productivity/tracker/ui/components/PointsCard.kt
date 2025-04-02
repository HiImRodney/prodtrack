package com.productivity.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivity.tracker.ui.theme.PointsColor

@Composable
fun PointsCard(
    totalPoints: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PointsColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TOTAL POINTS",
                style = MaterialTheme.typography.titleMedium,
                color = PointsColor,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🏆",  // trophy emoji
                    style = MaterialTheme.typography.headlineMedium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "$totalPoints",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = PointsColor
                )
            }
            
            // Add a little motivational message based on points
            val message = when {
                totalPoints >= 1000 -> "Incredible! You're a productivity master!"
                totalPoints >= 500 -> "Amazing progress! Keep up the great work!"
                totalPoints >= 100 -> "You're building great habits! Keep going!"
                totalPoints > 0 -> "Great start! Every point counts!"
                else -> "Start earning points by completing activities!"
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}
