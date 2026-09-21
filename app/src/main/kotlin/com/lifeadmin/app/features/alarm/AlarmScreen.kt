package com.lifeadmin.app.features.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lifeadmin.app.core.util.rememberAppContainer
import com.lifeadmin.app.features.home.HomeViewModel
import java.time.format.DateTimeFormatter

/**
 * Alarm/Reminders screen showing all scheduled reminders
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    navController: NavController,
    viewModel: HomeViewModel = HomeViewModel(
        reminderRepository = rememberAppContainer().reminderRepository,
        completeReminderUseCase = rememberAppContainer().completeReminderUseCase
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("Today") }
    
    // Filter items based on selection
    val displayedItems = remember(selectedFilter, uiState) {
        when (selectedFilter) {
            "Today" -> uiState.todayItems
            "Week" -> uiState.todayItems + uiState.tomorrowItems + uiState.upcomingItems
            "All" -> uiState.overdueItems + uiState.todayItems + uiState.tomorrowItems + uiState.upcomingItems
            else -> uiState.todayItems
        }
    }
    
    val completedCount = displayedItems.count { it.completed }
    val totalCount = displayedItems.size
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0EFFF))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF0EFFF)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reminders",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    IconButton(onClick = { /* TODO: Options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Color(0xFF1F2937)
                        )
                    }
                }
            }
            
            // Circular Progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressCard(
                    completed = completedCount,
                    total = totalCount
                )
            }
            
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(modifier = Modifier.padding(6.dp)) {
                        FilterChip(
                            text = "Today",
                            selected = selectedFilter == "Today",
                            onClick = { selectedFilter = "Today" }
                        )
                        FilterChip(
                            text = "Week",
                            selected = selectedFilter == "Week",
                            onClick = { selectedFilter = "Week" }
                        )
                        FilterChip(
                            text = "All",
                            selected = selectedFilter == "All",
                            onClick = { selectedFilter = "All" }
                        )
                    }
                }
            }
            
            // Time Range Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "09:00 AM",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Text(
                    text = "— 2h",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
                Text(
                    text = "11:00 PM",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
            
            // Section Header
            Text(
                text = when (selectedFilter) {
                    "Today" -> "Today's records"
                    "Week" -> "This week's records"
                    "All" -> "All records"
                    else -> "Records"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            
            // Reminders List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayedItems) { reminder ->
                    ReminderItemCard(
                        reminder = reminder,
                        onClick = { /* TODO: Edit reminder */ },
                        onComplete = { completed ->
                            viewModel.toggleComplete(reminder.id, completed)
                        },
                        onDelete = {
                            viewModel.deleteReminder(reminder.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CircularProgressCard(completed: Int, total: Int) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        // Outer ring
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(180.dp),
            color = Color(0xFF6366F1),
            strokeWidth = 16.dp,
            trackColor = Color(0xFFE0E7FF)
        )
        
        // Inner content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(progress * total).toInt()}/${total}",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6366F1)
                )
            )
            Text(
                text = "Tasks",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF6366F1) else Color.Transparent
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            ),
            color = if (selected) Color.White else Color(0xFF6B7280)
        )
    }
}

@Composable
fun ReminderItemCard(
    reminder: com.lifeadmin.app.domain.model.ReminderItem,
    onClick: () -> Unit,
    onComplete: (Boolean) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reminder.type.emoji,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                reminder.dueDate?.let {
                    Text(
                        text = it.format(DateTimeFormatter.ofPattern("hh:mm a")),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textDecoration = if (reminder.completed) 
                            androidx.compose.ui.text.style.TextDecoration.LineThrough 
                        else null
                    )
                }
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${reminder.amount?.toInt() ?: 0}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                )
                Text(
                    text = reminder.currency ?: "ml",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color(0xFF9CA3AF)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { 
                            Text(if (reminder.completed) "Mark Incomplete" else "Mark Complete") 
                        },
                        onClick = {
                            onComplete(!reminder.completed)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (reminder.completed) 
                                    Icons.Default.RadioButtonUnchecked 
                                else 
                                    Icons.Default.CheckCircle,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            onDelete()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFEF4444)
                            )
                        }
                    )
                }
            }
        }
    }
}
