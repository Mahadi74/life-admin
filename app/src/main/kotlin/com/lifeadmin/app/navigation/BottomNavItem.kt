package com.lifeadmin.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation items
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    
    object Alarm : BottomNavItem(
        route = "alarm",
        title = "Alarm",
        icon = Icons.Default.Alarm
    )
    
    object Statistics : BottomNavItem(
        route = "statistics",
        title = "Statistics",
        icon = Icons.Default.Insights
    )
    
    object Settings : BottomNavItem(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
    
    companion object {
        fun getItems() = listOf(Home, Alarm, Statistics, Settings)
    }
}

// Additional screens not in bottom nav
object Screens {
    const val SEARCH = "search"
    const val CALENDAR = "calendar"
    const val ITEM_DETAILS = "item_details/{itemId}"
    
    fun itemDetails(itemId: Long) = "item_details/$itemId"
}
