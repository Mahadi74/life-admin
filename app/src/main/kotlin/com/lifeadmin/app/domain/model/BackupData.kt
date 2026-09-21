package com.lifeadmin.app.domain.model

import kotlinx.serialization.Serializable

/**
 * Backup data model with versioned schema
 * Version 1: Initial backup format
 */
@Serializable
data class BackupData(
    val version: Int = CURRENT_VERSION,
    val exportDate: Long = System.currentTimeMillis(),
    val reminders: List<ReminderBackup>,
    val categories: List<CategoryBackup>
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}

/**
 * Serializable reminder for backup
 */
@Serializable
data class ReminderBackup(
    val id: Long,
    val title: String,
    val description: String? = null,
    val notes: String? = null,
    val type: String,
    val categoryId: Long? = null,
    val amount: Double? = null,
    val currency: String? = null,
    val dueDate: Long? = null,
    val reminderEnabled: Boolean = false,
    val reminderOffsetMinutes: Int? = null,
    val recurrenceType: String? = null,
    val completed: Boolean = false,
    val archived: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long,
    val parentId: Long? = null
)

/**
 * Serializable category for backup
 */
@Serializable
data class CategoryBackup(
    val id: Long,
    val name: String,
    val icon: String,
    val colorHex: String? = null,
    val isDefault: Boolean = false
)

/**
 * Import strategy
 */
enum class ImportStrategy {
    MERGE,    // Add to existing reminders
    REPLACE   // Clear all and import
}

/**
 * Import result
 */
data class ImportResult(
    val success: Boolean,
    val remindersImported: Int = 0,
    val categoriesImported: Int = 0,
    val errors: List<String> = emptyList()
)
