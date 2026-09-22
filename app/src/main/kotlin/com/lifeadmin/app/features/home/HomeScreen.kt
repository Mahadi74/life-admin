package com.lifeadmin.app.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lifeadmin.app.core.util.rememberAppContainer
import com.lifeadmin.app.features.home.components.*
import com.lifeadmin.app.navigation.Screen
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Home screen - the heart of Life Admin
 * 
 * Shows:
 * - Greeting and date
 * - Overdue items (if any)
 * - Today's items
 * - Tomorrow's items
 * - Upcoming items
 * - Empty states
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = HomeViewModel(
        context = LocalContext.current,
        reminderRepository = rememberAppContainer().reminderRepository,
        completeReminderUseCase = rememberAppContainer().completeReminderUseCase
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var showAddSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            HomeTopBar(
                greeting = uiState.greeting,
                date = uiState.currentDate,
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onCalendarClick = { navController.navigate(Screen.Calendar.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Reminder",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            onItemClick = { item ->
                navController.navigate(Screen.ItemDetails.createRoute(item.id))
            },
            onAddFirstClick = { showAddSheet = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
    
    // Add item sheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    greeting: String,
    date: String,
    onSearchClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
            IconButton(onClick = onCalendarClick) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendar"
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        ),
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onItemClick: (com.lifeadmin.app.domain.model.ReminderItem) -> Unit,
    onAddFirstClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        // Loading state
        uiState.isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        // First launch (no items)
        uiState.isFirstLaunch -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                FirstLaunchEmptyState(
                    onAddFirstClick = onAddFirstClick
                )
            }
        }
        
        // All caught up (has items but nothing due)
        uiState.isAllCaughtUp -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                AllCaughtUpEmptyState()
            }
        }
        
        // Has items to show
        else -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 100.dp // FAB space
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Overdue section
                if (uiState.shouldShowOverdue) {
                    item {
                        SectionHeader(
                            title = "OVERDUE",
                            count = uiState.overdueItems.size
                        )
                    }
                    
                    items(
                        items = uiState.overdueItems,
                        key = { it.id }
                    ) { item ->
                        ReminderCard(
                            reminder = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Today section
                if (uiState.shouldShowToday) {
                    item {
                        SectionHeader(
                            title = "TODAY",
                            count = uiState.todayItems.size
                        )
                    }
                    
                    items(
                        items = uiState.todayItems,
                        key = { it.id }
                    ) { item ->
                        ReminderCard(
                            reminder = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Tomorrow section
                if (uiState.shouldShowTomorrow) {
                    item {
                        SectionHeader(
                            title = "TOMORROW",
                            count = uiState.tomorrowItems.size
                        )
                    }
                    
                    items(
                        items = uiState.tomorrowItems,
                        key = { it.id }
                    ) { item ->
                        ReminderCard(
                            reminder = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                
                // Upcoming section
                if (uiState.shouldShowUpcoming) {
                    item {
                        SectionHeader(
                            title = "UPCOMING",
                            count = uiState.upcomingItems.size
                        )
                    }
                    
                    items(
                        items = uiState.upcomingItems,
                        key = { it.id }
                    ) { item ->
                        ReminderCard(
                            reminder = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
                
                // No upcoming message
                if (!uiState.shouldShowUpcoming && 
                    (uiState.shouldShowToday || uiState.shouldShowTomorrow || uiState.shouldShowOverdue)) {
                    item {
                        Spacer(modifier = Modifier.height(Spacing.lg))
                        NoUpcomingEmptyState()
                    }
                }
            }
        }
    }
}
