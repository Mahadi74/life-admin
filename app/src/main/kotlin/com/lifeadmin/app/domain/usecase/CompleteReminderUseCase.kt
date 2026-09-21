package com.lifeadmin.app.domain.usecase

import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.domain.repository.ReminderRepository
import java.time.Instant

/**
 * Complete a reminder and create next occurrence if recurring
 */
class CompleteReminderUseCase(
    private val reminderRepository: ReminderRepository,
    private val calculateNextOccurrence: CalculateNextOccurrenceUseCase
) {
    
    /**
     * Complete a reminder
     * If recurring, create the next occurrence
     */
    suspend fun execute(reminderId: Long) {
        val reminder = reminderRepository.getById(reminderId) ?: return
        
        // Mark current reminder as completed
        reminderRepository.complete(reminderId)
        
        // If recurring, create next occurrence
        if (reminder.recurrenceType !is RecurrenceType.None && reminder.dueDate != null) {
            createNextOccurrence(reminder)
        }
    }
    
    /**
     * Create the next occurrence of a recurring reminder
     */
    private suspend fun createNextOccurrence(original: ReminderItem) {
        val nextDueDate = calculateNextOccurrence.execute(
            currentDateTime = original.dueDate!!,
            recurrenceType = original.recurrenceType
        )
        
        val nextOccurrence = original.copy(
            id = 0, // New ID will be generated
            dueDate = nextDueDate,
            completed = false,
            archived = false,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            parentId = original.id // Track relationship
        )
        
        reminderRepository.create(nextOccurrence)
    }
}
