package com.lifeadmin.app.features.additem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Recurrence pattern selector
 */
@Composable
fun RecurrenceSelector(
    recurrenceType: RecurrenceType,
    onRecurrenceChange: (RecurrenceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Recurring",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = Spacing.xs)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            item {
                FilterChip(
                    selected = recurrenceType is RecurrenceType.None,
                    onClick = { onRecurrenceChange(RecurrenceType.None) },
                    label = { Text("One-time") }
                )
            }
            
            item {
                FilterChip(
                    selected = recurrenceType is RecurrenceType.Daily,
                    onClick = { onRecurrenceChange(RecurrenceType.Daily(1)) },
                    label = { Text("Daily") }
                )
            }
            
            item {
                FilterChip(
                    selected = recurrenceType is RecurrenceType.Weekly,
                    onClick = { onRecurrenceChange(RecurrenceType.Weekly(1)) },
                    label = { Text("Weekly") }
                )
            }
            
            item {
                FilterChip(
                    selected = recurrenceType is RecurrenceType.Monthly,
                    onClick = { onRecurrenceChange(RecurrenceType.Monthly(1)) },
                    label = { Text("Monthly") }
                )
            }
            
            item {
                FilterChip(
                    selected = recurrenceType is RecurrenceType.Yearly,
                    onClick = { onRecurrenceChange(RecurrenceType.Yearly(1)) },
                    label = { Text("Yearly") }
                )
            }
        }
        
        // Show interval selector for recurring types
        when (recurrenceType) {
            is RecurrenceType.Daily -> {
                if (recurrenceType.interval > 1) {
                    Text(
                        text = "Every ${recurrenceType.interval} days",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xs, start = Spacing.xs)
                    )
                }
            }
            is RecurrenceType.Weekly -> {
                if (recurrenceType.interval > 1) {
                    Text(
                        text = "Every ${recurrenceType.interval} weeks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xs, start = Spacing.xs)
                    )
                }
            }
            is RecurrenceType.Monthly -> {
                if (recurrenceType.interval > 1) {
                    Text(
                        text = "Every ${recurrenceType.interval} months",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xs, start = Spacing.xs)
                    )
                }
            }
            is RecurrenceType.Yearly -> {
                if (recurrenceType.interval > 1) {
                    Text(
                        text = "Every ${recurrenceType.interval} years",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xs, start = Spacing.xs)
                    )
                }
            }
            else -> {}
        }
    }
}
