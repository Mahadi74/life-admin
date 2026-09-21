package com.lifeadmin.app.features.additem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeadmin.app.domain.model.ItemType
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Selector for choosing reminder type
 * Shows chips with emoji icons for quick selection
 */
@Composable
fun TypeSelector(
    selectedType: ItemType,
    onTypeSelected: (ItemType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Type",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ),
            color = androidx.compose.ui.graphics.Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ItemType.values().toList()) { type ->
                FilterChip(
                    selected = type == selectedType,
                    onClick = { onTypeSelected(type) },
                    label = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = type.emoji,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = type.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        labelColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                )
            }
        }
    }
}
