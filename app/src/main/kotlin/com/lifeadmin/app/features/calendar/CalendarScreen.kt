package com.lifeadmin.app.features.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lifeadmin.app.features.home.components.ReminderCard
import com.lifeadmin.app.navigation.Screen
import com.lifeadmin.app.ui.theme.Dimensions
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

/**
 * Calendar screen with month view
 * Shows dates with reminders and allows filtering by date
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CalendarTopBar(
                currentMonth = uiState.currentMonth,
                onPreviousMonth = viewModel::onPreviousMonth,
                onNextMonth = viewModel::onNextMonth,
                onTodayClick = viewModel::onTodayClick,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Calendar grid
            MonthCalendar(
                month = uiState.currentMonth,
                selectedDate = uiState.selectedDate,
                datesWithReminders = uiState.datesWithReminders,
                onDateClick = viewModel::onDateSelected,
                modifier = Modifier.padding(Dimensions.medium)
            )
            
            // Selected date reminders
            if (uiState.hasSelectedDate) {
                Divider()
                
                if (uiState.hasRemindersOnSelectedDate) {
                    SelectedDateReminders(
                        date = uiState.selectedDate!!,
                        reminders = uiState.selectedDateReminders,
                        onReminderClick = { reminder ->
                            navController.navigate(Screen.ItemDetails.createRoute(reminder.id))
                        },
                        onClearSelection = viewModel::clearSelection
                    )
                } else {
                    NoRemindersForDate(
                        date = uiState.selectedDate!!,
                        onClearSelection = viewModel::clearSelection
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopBar(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            // Today button
            IconButton(onClick = onTodayClick) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Go to today"
                )
            }
            
            // Previous month
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous month"
                )
            }
            
            // Next month
            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next month"
                )
            }
        }
    )
}

@Composable
private fun MonthCalendar(
    month: YearMonth,
    selectedDate: LocalDate?,
    datesWithReminders: Set<LocalDate>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DayOfWeek.entries.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Dimensions.small))
        
        // Calendar grid
        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday
        val daysInMonth = month.lengthOfMonth()
        
        // Calculate weeks
        val totalCells = firstDayOfWeek + daysInMonth
        val weeks = (totalCells + 6) / 7
        
        for (week in 0 until weeks) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.xxxsmall),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (dayOfWeek in 0 until 7) {
                    val cellIndex = week * 7 + dayOfWeek
                    val dayOfMonth = cellIndex - firstDayOfWeek + 1
                    
                    if (dayOfMonth in 1..daysInMonth) {
                        val date = month.atDay(dayOfMonth)
                        CalendarDay(
                            date = date,
                            isSelected = date == selectedDate,
                            isToday = date == LocalDate.now(),
                            hasReminders = date in datesWithReminders,
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty cell
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasReminders: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(Dimensions.xxxsmall)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            // Dot indicator for reminders
            if (hasReminders && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
private fun SelectedDateReminders(
    date: LocalDate,
    reminders: List<com.lifeadmin.app.domain.model.ReminderItem>,
    onReminderClick: (com.lifeadmin.app.domain.model.ReminderItem) -> Unit,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${reminders.size} ${if (reminders.size == 1) "reminder" else "reminders"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                TextButton(onClick = onClearSelection) {
                    Text("Clear")
                }
            }
        }
        
        // Reminders list
        LazyColumn(
            contentPadding = PaddingValues(Dimensions.medium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.small)
        ) {
            items(
                items = reminders,
                key = { it.id }
            ) { reminder ->
                ReminderCard(
                    reminder = reminder,
                    onClick = { onReminderClick(reminder) }
                )
            }
        }
    }
}

@Composable
private fun NoRemindersForDate(
    date: LocalDate,
    onClearSelection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimensions.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(Dimensions.medium))
        
        Text(
            text = "No reminders on this date",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(Dimensions.small))
        
        TextButton(onClick = onClearSelection) {
            Text("Clear selection")
        }
    }
}
