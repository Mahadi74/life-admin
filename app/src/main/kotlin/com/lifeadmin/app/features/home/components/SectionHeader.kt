package com.lifeadmin.app.features.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Section header for grouping reminders
 * Modern, clean design with subtle styling
 */
@Composable
fun SectionHeader(
    title: String,
    count: Int? = null,
    modifier: Modifier = Modifier
) {
    val displayTitle = if (count != null && count > 0) {
        "$title  •  $count"
    } else {
        title
    }
    
    Text(
        text = displayTitle,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp,
                bottom = 8.dp,
                top = 16.dp
            )
    )
}
