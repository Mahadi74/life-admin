package com.lifeadmin.app.data.local

import com.lifeadmin.app.data.local.entity.CategoryEntity

/**
 * Default categories pre-populated on first app launch
 */
object DefaultCategories {
    
    fun getDefaultCategories(): List<CategoryEntity> {
        return listOf(
            CategoryEntity(
                id = 0,
                name = "Bills",
                icon = "💰",
                colorHex = "#FF5722",
                sortOrder = 0,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Subscriptions",
                icon = "📺",
                colorHex = "#2196F3",
                sortOrder = 1,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Documents",
                icon = "📄",
                colorHex = "#9C27B0",
                sortOrder = 2,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Warranty",
                icon = "📦",
                colorHex = "#FF9800",
                sortOrder = 3,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Health",
                icon = "🏥",
                colorHex = "#F44336",
                sortOrder = 4,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Finance",
                icon = "💳",
                colorHex = "#4CAF50",
                sortOrder = 5,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Home",
                icon = "🏠",
                colorHex = "#795548",
                sortOrder = 6,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Vehicle",
                icon = "🚗",
                colorHex = "#607D8B",
                sortOrder = 7,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Travel",
                icon = "✈️",
                colorHex = "#00BCD4",
                sortOrder = 8,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Work",
                icon = "💼",
                colorHex = "#3F51B5",
                sortOrder = 9,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Family",
                icon = "👨‍👩‍👧‍👦",
                colorHex = "#E91E63",
                sortOrder = 10,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Shopping",
                icon = "🛒",
                colorHex = "#FFC107",
                sortOrder = 11,
                isDefault = true
            ),
            CategoryEntity(
                id = 0,
                name = "Other",
                icon = "⭐",
                colorHex = "#9E9E9E",
                sortOrder = 12,
                isDefault = true
            )
        )
    }
}
