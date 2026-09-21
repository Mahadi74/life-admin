package com.lifeadmin.app.features.additem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Reminder notification settings
 */
@Composable
fun ReminderSettings(
    enabled: Boolean,
    offsetMinutes: Int,
    suggestedOffsets: List<Pair<String, Int>>,
    onEnabledChange: (Boolean) -> Unit,
    onOffsetChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Remind Me",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Get notified before it's due",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = enabled,
                onCheckedChange = onEnabledChange
            )
        }
        
        // Offset selector (shown when enabled)
        if (enabled) {
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                items(suggestedOffsets) { (label, minutes) ->
                    FilterChip(
                        selected = offsetMinutes == minutes,
                        onClick = { onOffsetChange(minutes) },
                        label = { Text(label) }
                    )
                }
            }
        }
    }
}
