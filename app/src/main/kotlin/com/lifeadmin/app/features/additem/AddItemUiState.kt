package com.lifeadmin.app.features.additem

import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.domain.model.ItemType
import com.lifeadmin.app.domain.model.RecurrenceType
import java.time.LocalDate
import java.time.LocalTime

/**
 * UI state for Add/Edit reminder flow
 * Progressive disclosure - fields appear based on selected type
 */
data class AddItemUiState(
    // Mode
    val isEditMode: Boolean = false,
    val itemId: Long? = null,
    
    // Basic fields
    val title: String = "",
    val titleError: String? = null,
    val description: String = "",
    val selectedType: ItemType = ItemType.REMINDER,
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    
    // Date/Time
    val selectedDate: LocalDate? = null,
    val selectedTime: LocalTime? = null,
    val dateError: String? = null,
    
    // Amount (for bills, subscriptions)
    val amount: String = "",
    val amountError: String? = null,
    val currency: String = "USD",
    
    // Reminder settings
    val reminderEnabled: Boolean = true,
    val reminderOffsetMinutes: Int = 1440, // 1 day before
    
    // Recurrence
    val recurrenceType: RecurrenceType = RecurrenceType.None,
    
    // State
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
) {
    /**
     * Check if amount field should be shown
     */
    val shouldShowAmount: Boolean
        get() = selectedType in listOf(
            ItemType.BILL,
            ItemType.SUBSCRIPTION,
            ItemType.RENEWAL
        )
    
    /**
     * Check if recurrence should default to enabled
     */
    val shouldDefaultRecurrence: Boolean
        get() = selectedType in listOf(
            ItemType.BILL,
            ItemType.SUBSCRIPTION
        )
    
    /**
     * Validate form
     */
    fun validate(): Boolean {
        return title.isNotBlank() &&
                titleError == null &&
                dateError == null &&
                amountError == null
    }
    
    /**
     * Get suggested reminder offsets based on type
     */
    fun getSuggestedReminderOffsets(): List<Pair<String, Int>> {
        return when (selectedType) {
            ItemType.BILL, ItemType.SUBSCRIPTION -> listOf(
                "1 day before" to 1440,
                "3 days before" to 4320,
                "1 week before" to 10080
            )
            ItemType.WARRANTY, ItemType.RETURN_DEADLINE, ItemType.DOCUMENT -> listOf(
                "1 week before" to 10080,
                "2 weeks before" to 20160,
                "1 month before" to 43200
            )
            ItemType.APPOINTMENT -> listOf(
                "1 hour before" to 60,
                "1 day before" to 1440,
                "3 days before" to 4320
            )
            else -> listOf(
                "At time" to 0,
                "1 hour before" to 60,
                "1 day before" to 1440
            )
        }
    }
}
