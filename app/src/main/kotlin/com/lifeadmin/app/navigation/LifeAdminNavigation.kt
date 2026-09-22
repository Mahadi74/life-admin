package com.lifeadmin.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lifeadmin.app.core.util.rememberAppContainer
import com.lifeadmin.app.features.calendar.CalendarScreen
import com.lifeadmin.app.features.calendar.CalendarViewModel
import com.lifeadmin.app.features.home.HomeScreen
import com.lifeadmin.app.features.itemdetails.ItemDetailsScreen
import com.lifeadmin.app.features.search.SearchScreen
import com.lifeadmin.app.features.search.SearchViewModel
import com.lifeadmin.app.features.settings.SettingsScreen
import com.lifeadmin.app.features.settings.SettingsViewModel

/**
 * Main navigation graph for Life Admin
 */
@Composable
fun LifeAdminNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(
            route = Screen.ItemDetails.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: return@composable
            ItemDetailsScreen(itemId = itemId, navController = navController)
        }
        
        composable(Screen.Calendar.route) {
            val appContainer = rememberAppContainer()
            val viewModel = remember {
                CalendarViewModel(
                    reminderRepository = appContainer.reminderRepository
                )
            }
            CalendarScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Search.route) {
            val appContainer = rememberAppContainer()
            val viewModel = remember {
                SearchViewModel(
                    reminderRepository = appContainer.reminderRepository,
                    categoryRepository = appContainer.categoryRepository
                )
            }
            SearchScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(Screen.Settings.route) {
            val appContainer = rememberAppContainer()
            val context = LocalContext.current
            val viewModel = remember {
                SettingsViewModel(
                    context = context,
                    backupRestoreUseCase = appContainer.backupRestoreUseCase
                )
            }
            SettingsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        // TODO: Add more destinations in later phases
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$title\nComing Soon",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}
