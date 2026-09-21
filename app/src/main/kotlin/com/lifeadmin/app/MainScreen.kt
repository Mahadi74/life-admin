package com.lifeadmin.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lifeadmin.app.features.alarm.AlarmScreen
import com.lifeadmin.app.features.home.HomeScreenContent
import com.lifeadmin.app.features.statistics.StatisticsScreen
import com.lifeadmin.app.navigation.BottomNavItem
import com.lifeadmin.app.navigation.Screens

/**
 * Main screen with bottom navigation
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom bar on certain screens
    val showBottomBar = currentRoute in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Alarm.route,
        BottomNavItem.Statistics.route,
        BottomNavItem.Settings.route
    )
    
    Scaffold(
        containerColor = Color(0xFFF0EFFF), // Light purple background
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreenContent(navController = navController)
            }
            composable(BottomNavItem.Alarm.route) {
                AlarmScreen(navController = navController)
            }
            composable(BottomNavItem.Statistics.route) {
                StatisticsScreen(navController = navController)
            }
            composable(BottomNavItem.Settings.route) {
                val appContainer = com.lifeadmin.app.core.util.rememberAppContainer()
                val settingsViewModel = remember {
                    com.lifeadmin.app.features.settings.SettingsViewModel(
                        backupRestoreUseCase = appContainer.backupRestoreUseCase
                    )
                }
                com.lifeadmin.app.features.settings.SettingsScreen(
                    navController = navController,
                    viewModel = settingsViewModel
                )
            }
            
            // Additional screens
            composable(Screens.SEARCH) {
                val appContainer = com.lifeadmin.app.core.util.rememberAppContainer()
                val searchViewModel = remember {
                    com.lifeadmin.app.features.search.SearchViewModel(
                        reminderRepository = appContainer.reminderRepository,
                        categoryRepository = appContainer.categoryRepository
                    )
                }
                com.lifeadmin.app.features.search.SearchScreen(
                    navController = navController,
                    viewModel = searchViewModel
                )
            }
            composable(Screens.CALENDAR) {
                val appContainer = com.lifeadmin.app.core.util.rememberAppContainer()
                val calendarViewModel = remember {
                    com.lifeadmin.app.features.calendar.CalendarViewModel(
                        reminderRepository = appContainer.reminderRepository
                    )
                }
                com.lifeadmin.app.features.calendar.CalendarScreen(
                    navController = navController,
                    viewModel = calendarViewModel
                )
            }
            composable(Screens.ITEM_DETAILS) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId")?.toLongOrNull() ?: 0L
                val appContainer = com.lifeadmin.app.core.util.rememberAppContainer()
                val detailsViewModel = remember {
                    com.lifeadmin.app.features.itemdetails.ItemDetailsViewModel(
                        itemId = itemId,
                        reminderRepository = appContainer.reminderRepository,
                        categoryRepository = appContainer.categoryRepository,
                        completeReminderUseCase = appContainer.completeReminderUseCase,
                        calculateNextOccurrence = appContainer.calculateNextOccurrenceUseCase,
                        savedStateHandle = androidx.lifecycle.SavedStateHandle()
                    )
                }
                com.lifeadmin.app.features.itemdetails.ItemDetailsScreen(
                    itemId = itemId,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = BottomNavItem.getItems()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { 
                    it.route == item.route 
                } == true
                
                BottomNavItem(
                    icon = item.icon,
                    label = item.title,
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = if (selected) Color(0xFF6366F1) else Color.Transparent,
        shadowElevation = if (selected) 6.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) Color.White else Color(0xFF9CA3AF),
                    modifier = Modifier.size(22.dp)
                )
                if (selected) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}
