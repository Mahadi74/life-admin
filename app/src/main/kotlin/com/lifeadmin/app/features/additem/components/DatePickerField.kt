package com.lifeadmin.app.features.additem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeadmin.app.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Date picker with quick select chips
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = "Due Date",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ),
            color = androidx.compose.ui.graphics.Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Quick select chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedDate == LocalDate.now(),
                    onClick = { onDateSelected(LocalDate.now()) },
                    label = { Text("Today") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        labelColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                )
            }
            
            item {
                FilterChip(
                    selected = selectedDate == LocalDate.now().plusDays(1),
                    onClick = { onDateSelected(LocalDate.now().plusDays(1)) },
                    label = { Text("Tomorrow") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        labelColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                )
            }
            
            item {
                FilterChip(
                    selected = selectedDate == LocalDate.now().plusWeeks(1),
                    onClick = { onDateSelected(LocalDate.now().plusWeeks(1)) },
                    label = { Text("Next Week") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        labelColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                )
            }
            
            item {
                FilterChip(
                    selected = selectedDate == LocalDate.now().plusMonths(1),
                    onClick = { onDateSelected(LocalDate.now().plusMonths(1)) },
                    label = { Text("1 Month") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                        selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        labelColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                )
            }
            
            item {
                FilterChip(
                    selected = false,
                    onClick = { showDatePicker = true },
                    label = { Text("Custom") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = androidx.compose.ui.graphics.Color.White,
                        labelColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                )
            }
        }
        
        // Selected date display
        if (selectedDate != null) {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                color = androidx.compose.ui.graphics.Color(0xFFE0E7FF)
            ) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    ),
                    color = androidx.compose.ui.graphics.Color(0xFF6366F1),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate?.atStartOfDay()
                ?.atZone(java.time.ZoneId.systemDefault())
                ?.toInstant()
                ?.toEpochMilli()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
