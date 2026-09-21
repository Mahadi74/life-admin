package com.lifeadmin.app.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * New home screen following water reminder app design
 */
@Composable
fun HomeScreenContent(
    navController: NavController,
    viewModel: HomeViewModel = HomeViewModel(
        reminderRepository = rememberAppContainer().reminderRepository,
        completeReminderUseCase = rememberAppContainer().completeReminderUseCase
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddSheet by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0EFFF))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                HomeHeader(
                    userName = "User",  // Can be made dynamic with SharedPreferences later
                    onNotificationClick = { 
                        // Navigate to Alarm tab
                        navController.navigate(com.lifeadmin.app.navigation.BottomNavItem.Alarm.route) {
                            popUpTo(com.lifeadmin.app.navigation.BottomNavItem.Home.route)
                            launchSingleTop = true
                        }
                    }
                )
            }
            
            // Search and Calendar button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SearchBar(
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            navController.navigate(com.lifeadmin.app.navigation.Screens.SEARCH)
                        }
                    )
                    CalendarButton(onClick = { 
                        navController.navigate(com.lifeadmin.app.navigation.Screens.CALENDAR)
                    })
                }
            }
            
            // Mini Calendar Widget
            item {
                MiniCalendarWidget(
                    selectedDate = selectedDate,
                    onDateSelected = { newDate ->
                        selectedDate = newDate
                    }
                )
            }
            
            // Quick Action Buttons (below calendar)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.CheckCircle,
                        label = "${uiState.todayItems.count { it.completed }}\nCompleted",
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Schedule,
                        label = "${uiState.todayItems.count { !it.completed }}\nPending",
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Upcoming,
                        label = "${uiState.tomorrowItems.size}\nTomorrow",
                        modifier = Modifier.weight(1f),
                        onClick = { }
                    )
                }
            }
            
            // Quick Stats
            item {
                QuickStatsRow(
                    todayCompleted = uiState.todayItems.count { it.completed },
                    todayTotal = uiState.todayItems.size,
                    overdueCount = uiState.overdueItems.size,
                    upcomingCount = uiState.upcomingItems.size
                )
            }
            
            // Daily Target Card
            item {
                DailyTargetCard(
                    completed = uiState.todayItems.count { it.completed },
                    total = uiState.todayItems.size
                )
            }
            
            // Today's Tasks Section
            item {
                Text(
                    text = "Today's Tasks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Tasks List
            items(uiState.todayItems) { item ->
                TaskCard(
                    reminder = item,
                    onClick = { 
                        navController.navigate(com.lifeadmin.app.navigation.Screens.itemDetails(item.id))
                    },
                    onComplete = { completed ->
                        viewModel.toggleComplete(item.id, completed)
                    }
                )
            }
        }
        
        // FAB
        FloatingActionButton(
            onClick = { showAddSheet = true },
            containerColor = Color(0xFF6366F1),
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                modifier = Modifier.size(32.dp)
            )
        }
    }
    
    // Add sheet
    if (showAddSheet) {
        val appContainer = rememberAppContainer()
        val addViewModel = remember {
            com.lifeadmin.app.features.additem.AddItemViewModel(
                reminderRepository = appContainer.reminderRepository,
                categoryRepository = appContainer.categoryRepository,
                notificationScheduler = appContainer.notificationScheduler,
                savedStateHandle = androidx.lifecycle.SavedStateHandle()
            )
        }
        
        com.lifeadmin.app.features.additem.AddItemSheet(
            viewModel = addViewModel,
            onDismiss = { showAddSheet = false }
        )
    }
}

@Composable
fun HomeHeader(userName: String, onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar with shadow
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color(0xFFE0E7FF),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userName.firstOrNull()?.toString()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Good Morning",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6B7280)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF6366F1)
                )
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
fun CalendarButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF6366F1),
        shadowElevation = 4.dp
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Calendar",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF6366F1),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF6B7280),
                fontSize = 9.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 11.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun MiniCalendarWidget(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFE0E7FF),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Week days
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(7) { index ->
                    val date = selectedDate.minusDays(3 - index.toLong())
                    DayItem(
                        date = date,
                        isSelected = date == selectedDate,
                        onClick = { onDateSelected(date) }
                    )
                }
            }
        }
    }
}

@Composable
fun DayItem(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (isSelected) Color(0xFF6366F1) else Color(0xFFF3F4F6),
        shadowElevation = if (isSelected) 3.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("E")),
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF6B7280),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) Color.White else Color(0xFF1F2937)
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    todayCompleted: Int,
    todayTotal: Int,
    overdueCount: Int,
    upcomingCount: Int
) {
    val completionRate = if (todayTotal > 0) {
        ((todayCompleted.toFloat() / todayTotal) * 100).toInt()
    } else {
        0
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Default.TrendingUp,
            value = "$completionRate%",
            label = "Today Rate",
            color = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.Warning,
            value = "$overdueCount",
            label = "Overdue",
            color = Color(0xFFEF4444),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Default.EventAvailable,
            value = "$upcomingCount",
            label = "Upcoming",
            color = Color(0xFF6366F1),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun DailyTargetCard(completed: Int, total: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE0E7FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Task Target",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "$completed of $total completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9CA3AF)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LinearProgressIndicator(
                    progress = if (total > 0) completed.toFloat() / total else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFE0E7FF)
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    reminder: com.lifeadmin.app.domain.model.ReminderItem,
    onClick: () -> Unit,
    onComplete: (Boolean) -> Unit = {}
) {
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
            // Checkbox
            Checkbox(
                checked = reminder.completed,
                onCheckedChange = onComplete,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF6366F1),
                    uncheckedColor = Color(0xFF9CA3AF),
                    checkmarkColor = Color.White
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = reminder.type.emoji,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textDecoration = if (reminder.completed) 
                        androidx.compose.ui.text.style.TextDecoration.LineThrough 
                    else null,
                    color = if (reminder.completed) Color(0xFF9CA3AF) else Color(0xFF1F2937)
                )
                reminder.dueDate?.let {
                    Text(
                        text = it.format(DateTimeFormatter.ofPattern("h:mm a")),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
            
            reminder.amount?.let { amount ->
                Text(
                    text = "${amount.toInt()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6366F1)
                    )
                )
            }
        }
    }
}
