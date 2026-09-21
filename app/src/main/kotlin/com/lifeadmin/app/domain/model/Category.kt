package com.lifeadmin.app.domain.model

import java.time.Instant

/**
 * Domain model for a category
 */
data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String, // Material icon name or emoji
    val colorHex: String? = null,
    val sortOrder: Int = 0,
    val isDefault: Boolean = true, // Default categories cannot be deleted
    val createdAt: Instant = Instant.now()
)
