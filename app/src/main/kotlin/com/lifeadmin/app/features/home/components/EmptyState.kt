package com.lifeadmin.app.features.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Empty state component with icon, message, and optional action
 * 2030-standard design with clear hierarchy and friendly messaging
 */
@Composable
fun EmptyState(
    icon: String,
    title: String,
    message: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon (emoji)
        Text(
            text = icon,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )
        
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )
        
        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )
        
        // Action button (optional)
        if (actionText != null && onActionClick != null) {
            Button(
                onClick = onActionClick,
                modifier = Modifier.padding(top = Spacing.sm)
            ) {
                Text(text = actionText)
            }
        }
    }
}

/**
 * First launch empty state
 */
@Composable
fun FirstLaunchEmptyState(
    onAddFirstClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = "✨",
        title = "Welcome to Life Admin",
        message = "Your personal reminder companion. Let's add your first reminder.",
        actionText = "Add Your First Reminder",
        onActionClick = onAddFirstClick,
        modifier = modifier
    )
}

/**
 * All caught up empty state
 */
@Composable
fun AllCaughtUpEmptyState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = "🎉",
        title = "You're all caught up!",
        message = "Nothing needs your attention right now.",
        modifier = modifier
    )
}

/**
 * No upcoming items empty state
 */
@Composable
fun NoUpcomingEmptyState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = "📅",
        title = "Nothing coming up",
        message = "Your schedule is clear.",
        modifier = modifier
    )
}
