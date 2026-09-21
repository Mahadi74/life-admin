package com.lifeadmin.app.domain.usecase

import android.content.Context
import com.lifeadmin.app.domain.model.*
import com.lifeadmin.app.domain.repository.CategoryRepository
import com.lifeadmin.app.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Use case for backing up and restoring reminder data
 */
class BackupRestoreUseCase(
    private val context: Context,
    private val reminderRepository: ReminderRepository,
    private val categoryRepository: CategoryRepository
) {
    
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    /**
     * Export all reminders and categories to JSON
     * Returns the backup file path
     */
    suspend fun exportToJson(): Result<String> {
        return try {
            // Fetch all data
            val reminders = reminderRepository.observeAll().first()
            val categories = categoryRepository.observeAll().first()
            
            // Convert to backup models
            val backupData = BackupData(
                reminders = reminders.map { it.toBackup() },
                categories = categories.map { it.toBackup() }
            )
            
            // Serialize to JSON
            val jsonString = json.encodeToString(backupData)
            
            // Save to file
            val fileName = "life_admin_backup_${getCurrentTimestamp()}.json"
            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(jsonString)
            
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Import reminders from JSON backup
     */
    suspend fun importFromJson(
        filePath: String,
        strategy: ImportStrategy
    ): Result<ImportResult> {
        return try {
            // Read file
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("Backup file not found"))
            }
            
            val jsonString = file.readText()
            
            // Parse JSON
            val backupData = json.decodeFromString<BackupData>(jsonString)
            
            // Validate version
            if (backupData.version > BackupData.CURRENT_VERSION) {
                return Result.failure(
                    Exception("Backup file is from a newer version. Please update the app.")
                )
            }
            
            // Clear existing data if REPLACE strategy
            if (strategy == ImportStrategy.REPLACE) {
                reminderRepository.deleteAll()
                // Note: Don't delete categories as they might be default
            }
            
            // Import categories first (non-default only)
            val importedCategories = backupData.categories
                .filter { !it.isDefault }
                .map { it.toDomain() }
            
            if (importedCategories.isNotEmpty()) {
                categoryRepository.createAll(importedCategories)
            }
            
            // Import reminders
            val importedReminders = backupData.reminders.map { it.toDomain() }
            
            if (importedReminders.isNotEmpty()) {
                reminderRepository.createAll(importedReminders)
            }
            
            Result.success(
                ImportResult(
                    success = true,
                    remindersImported = importedReminders.size,
                    categoriesImported = importedCategories.size
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Validate backup file without importing
     */
    suspend fun validateBackupFile(filePath: String): Result<BackupData> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(Exception("File not found"))
            }
            
            val jsonString = file.readText()
            val backupData = json.decodeFromString<BackupData>(jsonString)
            
            if (backupData.version > BackupData.CURRENT_VERSION) {
                return Result.failure(
                    Exception("Backup is from a newer app version")
                )
            }
            
            Result.success(backupData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getCurrentTimestamp(): String {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        )
    }
}

// Extension functions to convert between domain and backup models

private fun ReminderItem.toBackup(): ReminderBackup {
    return ReminderBackup(
        id = id,
        title = title,
        description = description,
        notes = notes,
        type = type.name,
        categoryId = categoryId,
        amount = amount,
        currency = currency,
        dueDate = dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
        reminderEnabled = reminderEnabled,
        reminderOffsetMinutes = reminderOffsetMinutes,
        recurrenceType = recurrenceType.toStorageString(),
        completed = completed,
        archived = archived,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        parentId = parentId
    )
}

private fun ReminderBackup.toDomain(): ReminderItem {
    return ReminderItem(
        id = id,
        title = title,
        description = description,
        notes = notes,
        type = ItemType.fromString(type),
        categoryId = categoryId,
        amount = amount,
        currency = currency,
        dueDate = dueDate?.let {
            LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        },
        reminderEnabled = reminderEnabled ?: false,
        reminderOffsetMinutes = reminderOffsetMinutes,
        recurrenceType = recurrenceType?.let { RecurrenceType.fromStorageString(it) } ?: RecurrenceType.None,
        completed = completed,
        archived = archived,
        createdAt = java.time.Instant.ofEpochMilli(createdAt),
        updatedAt = java.time.Instant.ofEpochMilli(updatedAt),
        parentId = parentId
    )
}

private fun Category.toBackup(): CategoryBackup {
    return CategoryBackup(
        id = id,
        name = name,
        icon = icon,
        colorHex = colorHex,
        isDefault = isDefault
    )
}

private fun CategoryBackup.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        icon = icon,
        colorHex = colorHex,
        isDefault = isDefault
    )
}
