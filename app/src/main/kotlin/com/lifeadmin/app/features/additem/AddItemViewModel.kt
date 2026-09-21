package com.lifeadmin.app.features.additem

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeadmin.app.core.util.CurrencyUtils
import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.domain.model.ItemType
import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.domain.repository.CategoryRepository
import com.lifeadmin.app.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * ViewModel for Add/Edit reminder flow
 * Handles progressive disclosure and smart defaults
 */
class AddItemViewModel(
    private val reminderRepository: ReminderRepository,
    private val categoryRepository: CategoryRepository,
    private val notificationScheduler: com.lifeadmin.app.core.notifications.NotificationScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val itemId: Long? = savedStateHandle.get<String>("itemId")?.toLongOrNull()
    
    private val _uiState = MutableStateFlow(AddItemUiState(
        isEditMode = itemId != null,
        itemId = itemId
    ))
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()
    
    init {
        loadCategories()
        if (itemId != null) {
            loadItem(itemId)
        }
    }
    
    /**
     * Load categories for selection
     */
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                categoryRepository.observeAll().collect { categories ->
                    _uiState.update { it.copy(categories = categories) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load categories") }
            }
        }
    }
    
    /**
     * Load existing item for editing
     */
    private fun loadItem(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val item = reminderRepository.getById(id)
                if (item != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            title = item.title,
                            description = item.description ?: "",
                            selectedType = item.type,
                            selectedCategory = null, // Will be populated from categories flow
                            selectedDate = item.dueDate?.toLocalDate(),
                            selectedTime = item.dueDate?.toLocalTime(),
                            amount = item.amount?.let { amt -> CurrencyUtils.formatAmountPlain(amt) } ?: "",
                            currency = item.currency ?: "USD",
                            reminderEnabled = item.reminderEnabled,
                            reminderOffsetMinutes = item.reminderOffsetMinutes ?: 1440,
                            recurrenceType = item.recurrenceType
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Item not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load item"
                    )
                }
            }
        }
    }
    
    // ===== UI Events =====
    
    fun onTitleChange(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                titleError = if (title.isBlank()) "Title is required" else null
            )
        }
    }
    
    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    fun onTypeSelected(type: ItemType) {
        _uiState.update { currentState ->
            val newRecurrence = if (type in listOf(ItemType.BILL, ItemType.SUBSCRIPTION)) {
                RecurrenceType.Monthly(1)
            } else {
                RecurrenceType.None
            }
            
            currentState.copy(
                selectedType = type,
                recurrenceType = newRecurrence
            )
        }
    }
    
    fun onCategorySelected(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    
    fun onDateSelected(date: LocalDate?) {
        _uiState.update {
            it.copy(
                selectedDate = date,
                dateError = null
            )
        }
    }
    
    fun onTimeSelected(time: LocalTime?) {
        _uiState.update { it.copy(selectedTime = time) }
    }
    
    fun onAmountChange(amountStr: String) {
        val parsed = CurrencyUtils.parseAmount(amountStr)
        _uiState.update {
            it.copy(
                amount = amountStr,
                amountError = if (it.shouldShowAmount && parsed == null && amountStr.isNotBlank()) {
                    "Invalid amount"
                } else null
            )
        }
    }
    
    fun onCurrencyChange(currency: String) {
        _uiState.update { it.copy(currency = currency) }
    }
    
    fun onReminderEnabledChange(enabled: Boolean) {
        _uiState.update { it.copy(reminderEnabled = enabled) }
    }
    
    fun onReminderOffsetChange(minutes: Int) {
        _uiState.update { it.copy(reminderOffsetMinutes = minutes) }
    }
    
    fun onRecurrenceChange(recurrence: RecurrenceType) {
        _uiState.update { it.copy(recurrenceType = recurrence) }
    }
    
    /**
     * Save the reminder
     */
    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        // Validate
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                val dueDateTime = if (state.selectedDate != null) {
                    LocalDateTime.of(
                        state.selectedDate,
                        state.selectedTime ?: LocalTime.of(9, 0)
                    )
                } else null
                
                val parsedAmount = if (state.shouldShowAmount && state.amount.isNotBlank()) {
                    CurrencyUtils.parseAmount(state.amount)
                } else null
                
                val reminder = ReminderItem(
                    id = state.itemId ?: 0,
                    title = state.title.trim(),
                    description = state.description.trim().takeIf { it.isNotBlank() },
                    categoryId = state.selectedCategory?.id,
                    type = state.selectedType,
                    amount = parsedAmount,
                    currency = if (parsedAmount != null) state.currency else null,
                    dueDate = dueDateTime,
                    reminderEnabled = state.reminderEnabled && dueDateTime != null,
                    reminderOffsetMinutes = if (state.reminderEnabled) state.reminderOffsetMinutes else null,
                    recurrenceType = state.recurrenceType,
                    completed = false,
                    archived = false,
                    createdAt = Instant.now(),
                    updatedAt = Instant.now()
                )
                
                if (state.isEditMode && state.itemId != null) {
                    reminderRepository.update(reminder)
                } else {
                    reminderRepository.create(reminder)
                }
                
                // Schedule notification if enabled
                if (reminder.reminderEnabled && reminder.dueDate != null) {
                    notificationScheduler.schedule(reminder)
                }
                
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to save reminder"
                    )
                }
            }
        }
    }
}
