package com.lifeadmin.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lifeadmin.app.domain.model.Category
import java.time.Instant

/**
 * Room entity for categories
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    val icon: String,
    
    @ColumnInfo(name = "color_hex")
    val colorHex: String? = null,
    
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
    
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = true,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Convert entity to domain model
     */
    fun toDomain(): Category {
        return Category(
            id = id,
            name = name,
            icon = icon,
            colorHex = colorHex,
            sortOrder = sortOrder,
            isDefault = isDefault,
            createdAt = Instant.ofEpochMilli(createdAt)
        )
    }
    
    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromDomain(category: Category): CategoryEntity {
            return CategoryEntity(
                id = category.id,
                name = category.name,
                icon = category.icon,
                colorHex = category.colorHex,
                sortOrder = category.sortOrder,
                isDefault = category.isDefault,
                createdAt = category.createdAt.toEpochMilli()
            )
        }
    }
}
