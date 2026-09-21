package com.lifeadmin.app.features.home

import com.lifeadmin.app.domain.model.ReminderItem

/**
 * UI state for the Home screen
 * Immutable data class for predictable state management
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val overdueItems: List<ReminderItem> = emptyList(),
    val todayItems: List<ReminderItem> = emptyList(),
    val tomorrowItems: List<ReminderItem> = emptyList(),
    val upcomingItems: List<ReminderItem> = emptyList(),
    val hasAnyItems: Boolean = false,
    val greeting: String = "",
    val currentDate: String = "",
    val error: String? = null
) {
    /**
     * Check if this is the first launch (no items at all)
     */
    val isFirstLaunch: Boolean
        get() = !isLoading && !hasAnyItems
    
    /**
     * Check if all caught up (has items but nothing due)
     */
    val isAllCaughtUp: Boolean
        get() = !isLoading && 
                hasAnyItems && 
                overdueItems.isEmpty() && 
                todayItems.isEmpty() && 
                tomorrowItems.isEmpty()
    
    /**
     * Check if should show overdue section
     */
    val shouldShowOverdue: Boolean
        get() = overdueItems.isNotEmpty()
    
    /**
     * Check if should show today section
     */
    val shouldShowToday: Boolean
        get() = todayItems.isNotEmpty()
    
    /**
     * Check if should show tomorrow section
     */
    val shouldShowTomorrow: Boolean
        get() = tomorrowItems.isNotEmpty()
    
    /**
     * Check if should show upcoming section
     */
    val shouldShowUpcoming: Boolean
        get() = upcomingItems.isNotEmpty()
}
