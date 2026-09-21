package com.lifeadmin.app.domain.model

/**
 * Types of reminders that can be created
 * Each type may have different default fields and behavior
 */
enum class ItemType(val displayName: String, val emoji: String) {
    REMINDER("Reminder", "💡"),
    BILL("Bill", "💰"),
    SUBSCRIPTION("Subscription", "📺"),
    WARRANTY("Warranty", "📦"),
    RETURN_DEADLINE("Return Deadline", "🔄"),
    DOCUMENT("Document", "📄"),
    APPOINTMENT("Appointment", "📅"),
    RENEWAL("Renewal", "🔄"),
    TASK("Task", "✓"),
    CUSTOM("Custom", "⭐");
    
    companion object {
        fun fromString(value: String): ItemType {
            return values().find { it.name == value } ?: CUSTOM
        }
    }
}
