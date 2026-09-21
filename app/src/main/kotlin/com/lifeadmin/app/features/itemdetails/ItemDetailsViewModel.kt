package com.lifeadmin.app.features.itemdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.domain.repository.CategoryRepository
import com.lifeadmin.app.domain.repository.ReminderRepository
import com.lifeadmin.app.domain.usecase.CalculateNextOccurrenceUseCase
import com.lifeadmin.app.domain.usecase.CompleteReminderUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for Item Details screen
 */
class ItemDetailsViewModel(
    private val itemId: Long,
    private val reminderRepository: ReminderRepository,
    private val categoryRepository: CategoryRepository,
    private val completeReminderUseCase: CompleteReminderUseCase,
    private val calculateNextOccurrence: CalculateNextOccurrenceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ItemDetailsUiState())
    val uiState: StateFlow<ItemDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadItem()
    }
    
    /**
     * Load item details
     */
    private fun loadItem() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                reminderRepository.observeById(itemId).collect { item ->
                    if (item != null) {
                        // Load category
                        val category = item.categoryId?.let { categoryId ->
                            categoryRepository.getById(categoryId)
                        }
                        
                        // Calculate next occurrences for recurring items
                        val nextOccurrences = if (item.recurrenceType !is RecurrenceType.None && 
                                                   item.dueDate != null) {
                            calculateNextOccurrence.calculateMultipleOccurrences(
                                startDateTime = item.dueDate,
                                recurrenceType = item.recurrenceType,
                                count = 5
                            )
                        } else {
                            emptyList()
                        }
                        
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                item = item,
                                category = category,
                                nextOccurrences = nextOccurrences,
                                error = null
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
    
    /**
     * Complete the reminder
     */
    fun complete(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                completeReminderUseCase.execute(itemId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to complete reminder") }
            }
        }
    }
    
    /**
     * Uncomplete the reminder
     */
    fun uncomplete() {
        viewModelScope.launch {
            try {
                reminderRepository.uncomplete(itemId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to uncomplete reminder") }
            }
        }
    }
    
    /**
     * Snooze reminder to a specific date
     */
    fun snooze(days: Long) {
        viewModelScope.launch {
            try {
                val item = _uiState.value.item ?: return@launch
                val newDueDate = item.dueDate?.plusDays(days) ?: return@launch
                
                val updatedItem = item.copy(
                    dueDate = newDueDate,
                    updatedAt = java.time.Instant.now()
                )
                
                reminderRepository.update(updatedItem)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to snooze reminder") }
            }
        }
    }
    
    /**
     * Archive the reminder
     */
    fun archive(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                reminderRepository.archive(itemId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to archive reminder") }
            }
        }
    }
    
    /**
     * Delete the reminder
     */
    fun delete(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                reminderRepository.delete(itemId)
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete reminder") }
            }
        }
    }
    
    /**
     * Show delete confirmation dialog
     */
    fun showDeleteConfirmation(show: Boolean) {
        _uiState.update { it.copy(showDeleteConfirmation = show) }
    }
    
    /**
     * Show archive confirmation dialog
     */
    fun showArchiveConfirmation(show: Boolean) {
        _uiState.update { it.copy(showArchiveConfirmation = show) }
    }
}
