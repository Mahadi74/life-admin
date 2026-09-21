package com.lifeadmin.app.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model for a reminder item
 * Clean representation without database annotations
 */
data class ReminderItem(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val notes: String? = null,
    val categoryId: Long? = null,
    val type: ItemType,
    val amount: Double? = null,
    val currency: String? = null,
    val dueDate: LocalDateTime? = null,
    val reminderEnabled: Boolean = false,
    val reminderOffsetMinutes: Int? = null,
    val recurrenceType: RecurrenceType = RecurrenceType.None,
    val completed: Boolean = false,
    val archived: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val parentId: Long? = null // For tracking recurring instances
) {
    /**
     * Check if this reminder is overdue
     */
    fun isOverdue(): Boolean {
        if (completed || archived) return false
        val due = dueDate ?: return false
        return due.isBefore(LocalDateTime.now())
    }
    
    /**
     * Check if this reminder is due today
     */
    fun isDueToday(): Boolean {
        if (completed || archived) return false
        val due = dueDate ?: return false
        val today = LocalDate.now()
        return due.toLocalDate() == today
    }
    
    /**
     * Check if this reminder is due tomorrow
     */
    fun isDueTomorrow(): Boolean {
        if (completed || archived) return false
        val due = dueDate ?: return false
        val tomorrow = LocalDate.now().plusDays(1)
        return due.toLocalDate() == tomorrow
    }
    
    /**
     * Get days until due (negative if overdue)
     */
    fun daysUntilDue(): Long? {
        if (completed || archived) return null
        val due = dueDate ?: return null
        val now = LocalDateTime.now()
        return java.time.Duration.between(now, due).toDays()
    }
}
