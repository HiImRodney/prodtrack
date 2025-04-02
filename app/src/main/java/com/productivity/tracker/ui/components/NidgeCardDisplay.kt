package com.productivity.tracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.productivity.tracker.R
import com.productivity.tracker.ui.theme.StreakGold

@Composable
fun NidgeCardDisplay(
    canUseNidgeCard: Boolean,
    nidgeCardUsed: Boolean,
    onUseNidgeCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                nidgeCardUsed -> MaterialTheme.colorScheme.surfaceVariant
                canUseNidgeCard -> StreakGold.copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title with emoji
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🎟️",  // ticket emoji
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = stringResource(R.string.nidge_card),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status text
            Text(
                text = when {
                    nidgeCardUsed -> stringResource(R.string.nidge_card_used)
                    canUseNidgeCard -> stringResource(R.string.nidge_card_available)
                    else -> stringResource(R.string.nidge_card_description)
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Use button (only if available)
            if (canUseNidgeCard) {
                Button(
                    onClick = onUseNidgeCard,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreakGold
                    )
                ) {
                    Text(stringResource(R.string.nidge_card_use))
                }
            }
        }
    }
}
