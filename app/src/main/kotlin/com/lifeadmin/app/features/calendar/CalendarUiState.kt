package com.lifeadmin.app.features.calendar

import com.lifeadmin.app.domain.model.ReminderItem
import java.time.LocalDate
import java.time.YearMonth

/**
 * UI state for calendar screen
 */
data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val datesWithReminders: Set<LocalDate> = emptySet(),
    val selectedDateReminders: List<ReminderItem> = emptyList(),
    val isLoading: Boolean = true
) {
    val hasSelectedDate: Boolean
        get() = selectedDate != null
    
    val hasRemindersOnSelectedDate: Boolean
        get() = selectedDateReminders.isNotEmpty()
}
