package com.lifeadmin.app.features.itemdetails

import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.domain.model.ReminderItem

/**
 * UI state for Item Details screen
 */
data class ItemDetailsUiState(
    val isLoading: Boolean = true,
    val item: ReminderItem? = null,
    val category: Category? = null,
    val nextOccurrences: List<java.time.LocalDateTime> = emptyList(),
    val error: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val showArchiveConfirmation: Boolean = false
) {
    /**
     * Check if item has loaded
     */
    val isItemLoaded: Boolean
        get() = !isLoading && item != null
    
    /**
     * Get countdown text
     */
    fun getCountdownText(): String? {
        val daysUntil = item?.daysUntilDue()
        return when {
            daysUntil == null -> null
            daysUntil < 0 -> "${-daysUntil} days overdue"
            daysUntil == 0L -> "Due today"
            daysUntil == 1L -> "Due tomorrow"
            daysUntil < 7 -> "Due in $daysUntil days"
            daysUntil < 30 -> "Due in ${daysUntil / 7} weeks"
            else -> "Due in ${daysUntil / 30} months"
        }
    }
}
