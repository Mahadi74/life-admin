package com.lifeadmin.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lifeadmin.app.domain.model.ItemType
import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.domain.model.ReminderItem
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Room entity for reminders
 * Optimized with indices for common queries
 */
@Entity(
    tableName = "reminders",
    indices = [
        Index("due_date"),
        Index("category_id"),
        Index("completed"),
        Index("archived"),
        Index("type")
    ]
)
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    val description: String? = null,
    val notes: String? = null,
    
    @ColumnInfo(name = "category_id")
    val categoryId: Long? = null,
    
    @ColumnInfo(name = "type")
    val type: String, // ItemType.name
    
    val amount: Double? = null,
    val currency: String? = null,
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long? = null, // Epoch millis (LocalDateTime)
    
    @ColumnInfo(name = "reminder_enabled")
    val reminderEnabled: Boolean = false,
    
    @ColumnInfo(name = "reminder_offset_minutes")
    val reminderOffsetMinutes: Int? = null,
    
    @ColumnInfo(name = "recurrence_type")
    val recurrenceType: String, // RecurrenceType serialized
    
    val completed: Boolean = false,
    val archived: Boolean = false,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "parent_id")
    val parentId: Long? = null
) {
    /**
     * Convert entity to domain model
     */
    fun toDomain(): ReminderItem {
        return ReminderItem(
            id = id,
            title = title,
            description = description,
            notes = notes,
            categoryId = categoryId,
            type = ItemType.fromString(type),
            amount = amount,
            currency = currency,
            dueDate = dueDate?.let { 
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
            },
            reminderEnabled = reminderEnabled,
            reminderOffsetMinutes = reminderOffsetMinutes,
            recurrenceType = RecurrenceType.fromStorageString(recurrenceType),
            completed = completed,
            archived = archived,
            createdAt = Instant.ofEpochMilli(createdAt),
            updatedAt = Instant.ofEpochMilli(updatedAt),
            parentId = parentId
        )
    }
    
    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromDomain(item: ReminderItem): ReminderEntity {
            return ReminderEntity(
                id = item.id,
                title = item.title,
                description = item.description,
                notes = item.notes,
                categoryId = item.categoryId,
                type = item.type.name,
                amount = item.amount,
                currency = item.currency,
                dueDate = item.dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
                reminderEnabled = item.reminderEnabled,
                reminderOffsetMinutes = item.reminderOffsetMinutes,
                recurrenceType = item.recurrenceType.toStorageString(),
                completed = item.completed,
                archived = item.archived,
                createdAt = item.createdAt.toEpochMilli(),
                updatedAt = item.updatedAt.toEpochMilli(),
                parentId = item.parentId
            )
        }
    }
}
