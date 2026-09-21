package com.lifeadmin.app.domain.model

/**
 * Recurrence patterns for repeating reminders
 * Stores both the type and interval (e.g., every 2 weeks, every 3 months)
 */
sealed class RecurrenceType {
    object None : RecurrenceType()
    data class Daily(val interval: Int = 1) : RecurrenceType()
    data class Weekly(val interval: Int = 1) : RecurrenceType()
    data class Monthly(val interval: Int = 1) : RecurrenceType()
    data class Yearly(val interval: Int = 1) : RecurrenceType()
    
    /**
     * Serialize to string for database storage
     */
    fun toStorageString(): String = when (this) {
        is None -> "NONE"
        is Daily -> "DAILY:$interval"
        is Weekly -> "WEEKLY:$interval"
        is Monthly -> "MONTHLY:$interval"
        is Yearly -> "YEARLY:$interval"
    }
    
    companion object {
        /**
         * Deserialize from database string
         */
        fun fromStorageString(value: String): RecurrenceType {
            if (value == "NONE") return None
            
            val parts = value.split(":")
            if (parts.size != 2) return None
            
            val type = parts[0]
            val interval = parts[1].toIntOrNull() ?: 1
            
            return when (type) {
                "DAILY" -> Daily(interval)
                "WEEKLY" -> Weekly(interval)
                "MONTHLY" -> Monthly(interval)
                "YEARLY" -> Yearly(interval)
                else -> None
            }
        }
    }
}
