package com.lifeadmin.app.features.search

import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.domain.model.ReminderItem

/**
 * UI state for search screen
 */
data class SearchUiState(
    val searchQuery: String = "",
    val selectedFilter: SearchFilter = SearchFilter.All,
    val selectedCategory: Category? = null,
    val results: List<ReminderItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isSearching: Boolean = false,
    val hasSearched: Boolean = false
) {
    val isEmpty: Boolean
        get() = hasSearched && results.isEmpty()
}

/**
 * Filter options for search
 */
enum class SearchFilter(val displayName: String) {
    All("All"),
    Today("Today"),
    Upcoming("Upcoming"),
    Overdue("Overdue"),
    Completed("Completed"),
    Archived("Archived")
}
