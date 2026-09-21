package com.lifeadmin.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeadmin.app.core.util.DateUtils
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.domain.repository.ReminderRepository
import com.lifeadmin.app.domain.usecase.CompleteReminderUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel for the Home screen
 * Handles business logic and state management
 */
class HomeViewModel(
    private val reminderRepository: ReminderRepository,
    private val completeReminderUseCase: CompleteReminderUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadReminders()
    }
    
    /**
     * Load and observe reminders from repository
     */
    private fun loadReminders() {
        viewModelScope.launch {
            try {
                // Combine multiple flows into single UI state
                combine(
                    reminderRepository.observeOverdue(),
                    reminderRepository.observeToday(),
                    reminderRepository.observeUpcoming(limit = 20),
                    reminderRepository.observeActiveCount()
                ) { overdue, today, upcoming, totalCount ->
                    // Split upcoming into tomorrow and rest
                    val tomorrow = LocalDate.now().plusDays(1)
                    val tomorrowItems = upcoming.filter { item ->
                        item.dueDate?.toLocalDate() == tomorrow
                    }
                    val laterItems = upcoming.filter { item ->
                        val dueDate = item.dueDate?.toLocalDate()
                        dueDate != null && dueDate.isAfter(tomorrow)
                    }
                    
                    HomeUiState(
                        isLoading = false,
                        overdueItems = overdue,
                        todayItems = today,
                        tomorrowItems = tomorrowItems,
                        upcomingItems = laterItems,
                        hasAnyItems = totalCount > 0,
                        greeting = getGreeting(),
                        currentDate = getCurrentDate(),
                        error = null
                    )
                }.catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    /**
     * Toggle completion status
     */
    fun toggleComplete(id: Long, completed: Boolean) {
        viewModelScope.launch {
            try {
                if (completed) {
                    completeReminderUseCase.execute(id)
                } else {
                    reminderRepository.uncomplete(id)
                }
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }
    
    /**
     * Complete a reminder (and create next occurrence if recurring)
     */
    fun completeReminder(id: Long) {
        viewModelScope.launch {
            try {
                completeReminderUseCase.execute(id)
            } catch (e: Exception) {
                // Handle error silently for now
                // TODO: Show user-friendly error message
            }
        }
    }
    
    /**
     * Uncomplete a reminder
     */
    fun uncompleteReminder(id: Long) {
        viewModelScope.launch {
            try {
                reminderRepository.uncomplete(id)
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }
    
    /**
     * Delete a reminder
     */
    fun deleteReminder(id: Long) {
        viewModelScope.launch {
            try {
                reminderRepository.delete(id)
            } catch (e: Exception) {
                // Handle error silently for now
            }
        }
    }
    
    /**
     * Get contextual greeting based on time of day
     */
    private fun getGreeting(): String {
        val hour = LocalTime.now().hour
        return when (hour) {
            in 0..4 -> "Good night 🌙"
            in 5..11 -> "Good morning ☀️"
            in 12..17 -> "Good afternoon 👋"
            in 18..21 -> "Good evening 🌆"
            else -> "Good night 🌙"
        }
    }
    
    /**
     * Get current date formatted for display
     */
    private fun getCurrentDate(): String {
        return LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d")
        )
    }
}
