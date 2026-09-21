package com.lifeadmin.app.features.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

/**
 * ViewModel for calendar screen
 * Manages month navigation and date selection
 */
class CalendarViewModel(
    private val reminderRepository: ReminderRepository
) : ViewModel() {
    
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    
    // All active reminders (for calculating dates with reminders)
    private val allReminders: StateFlow<List<ReminderItem>> = reminderRepository
        .observeActive()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Reminders for selected date
    private val selectedDateReminders: StateFlow<List<ReminderItem>> = _selectedDate
        .flatMapLatest { date ->
            if (date != null) {
                reminderRepository.observeByDateRange(date, date)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Dates with reminders in current month
    private val datesWithReminders: StateFlow<Set<LocalDate>> = combine(
        _currentMonth,
        allReminders
    ) { month, reminders ->
        reminders
            .mapNotNull { it.dueDate?.toLocalDate() }
            .filter { date ->
                date.year == month.year && date.monthValue == month.monthValue
            }
            .toSet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )
    
    // Combined UI state
    val uiState: StateFlow<CalendarUiState> = combine(
        _currentMonth,
        _selectedDate,
        datesWithReminders,
        selectedDateReminders,
        allReminders
    ) { month, selectedDate, datesWithReminders, dateReminders, allReminders ->
        CalendarUiState(
            currentMonth = month,
            selectedDate = selectedDate,
            datesWithReminders = datesWithReminders,
            selectedDateReminders = dateReminders,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState(isLoading = true)
    )
    
    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = if (_selectedDate.value == date) {
            null // Deselect if already selected
        } else {
            date
        }
    }
    
    fun onPreviousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        _selectedDate.value = null // Clear selection when changing months
    }
    
    fun onNextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        _selectedDate.value = null // Clear selection when changing months
    }
    
    fun onTodayClick() {
        val today = LocalDate.now()
        _currentMonth.value = YearMonth.from(today)
        _selectedDate.value = today
    }
    
    fun clearSelection() {
        _selectedDate.value = null
    }
}
