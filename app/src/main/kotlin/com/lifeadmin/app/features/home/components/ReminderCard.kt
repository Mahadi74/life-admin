package com.lifeadmin.app.features.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lifeadmin.app.core.util.CurrencyUtils
import com.lifeadmin.app.core.util.DateUtils
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.ui.theme.CornerRadius
import com.lifeadmin.app.ui.theme.Dimensions
import com.lifeadmin.app.ui.theme.Spacing
import com.lifeadmin.app.ui.theme.StatusColors

/**
 * Modern reminder card inspired by contemporary task management apps
 * Features: checkbox, clean layout, subtle elevation
 */
@Composable
fun ReminderCard(
    reminder: ReminderItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Checkbox indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        color = getStatusColor(reminder).copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(getStatusColor(reminder))
                )
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Metadata row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Date chip
                    reminder.dueDate?.let { dueDate ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = DateUtils.getRelativeDescription(dueDate),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Amount chip
                    reminder.amount?.let { amount ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = getStatusColor(reminder).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = CurrencyUtils.formatAmount(amount, reminder.currency),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = getStatusColor(reminder),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Type emoji
                    if (reminder.type.displayName != "Reminder") {
                        Text(
                            text = reminder.type.emoji,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Get color for status indication based on reminder state
 */
@Composable
private fun getStatusColor(reminder: ReminderItem): Color {
    return when {
        reminder.isOverdue() -> StatusColors.overdue
        reminder.isDueToday() -> StatusColors.dueToday
        reminder.isDueTomorrow() -> StatusColors.dueSoon
        else -> StatusColors.upcoming
    }
}
