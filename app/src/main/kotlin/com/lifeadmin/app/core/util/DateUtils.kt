package com.lifeadmin.app.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

/**
 * Date and time utility functions
 * Handles formatting, grouping, and calculations
 */
object DateUtils {
    
    /**
     * Format date for display
     * Examples: "Today", "Tomorrow", "Sep 25", "Dec 31, 2027"
     */
    fun formatDateForDisplay(dateTime: LocalDateTime?): String {
        if (dateTime == null) return ""
        
        val date = dateTime.toLocalDate()
        val today = LocalDate.now()
        
        return when {
            date == today -> "Today"
            date == today.plusDays(1) -> "Tomorrow"
            date == today.minusDays(1) -> "Yesterday"
            date.year == today.year -> {
                // Same year: "Sep 25"
                date.format(DateTimeFormatter.ofPattern("MMM d"))
            }
            else -> {
                // Different year: "Dec 31, 2027"
                date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
            }
        }
    }
    
    /**
     * Format date and time for display
     * Examples: "Today at 2:30 PM", "Sep 25 at 9:00 AM"
     */
    fun formatDateTimeForDisplay(dateTime: LocalDateTime?): String {
        if (dateTime == null) return ""
        
        val datePart = formatDateForDisplay(dateTime)
        val timePart = dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        
        return "$datePart at $timePart"
    }
    
    /**
     * Format date in full format
     * Example: "Sunday, September 20, 2026"
     */
    fun formatDateLong(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    }
    
    /**
     * Get relative time description
     * Examples: "Due today", "Due tomorrow", "Overdue", "Due in 5 days"
     */
    fun getRelativeDescription(dateTime: LocalDateTime?): String {
        if (dateTime == null) return ""
        
        val date = dateTime.toLocalDate()
        val today = LocalDate.now()
        val daysDiff = ChronoUnit.DAYS.between(today, date)
        
        return when {
            daysDiff < 0 -> "Overdue"
            daysDiff == 0L -> "Due today"
            daysDiff == 1L -> "Due tomorrow"
            daysDiff in 2..6 -> "Due in $daysDiff days"
            daysDiff in 7..13 -> "Due next week"
            daysDiff in 14..30 -> "Due in ${daysDiff / 7} weeks"
            else -> "Due in ${daysDiff / 30} months"
        }
    }
    
    /**
     * Calculate days until date (negative if past)
     */
    fun daysUntil(dateTime: LocalDateTime?): Long? {
        if (dateTime == null) return null
        val now = LocalDateTime.now()
        return ChronoUnit.DAYS.between(now, dateTime)
    }
    
    /**
     * Check if date is today
     */
    fun isToday(dateTime: LocalDateTime?): Boolean {
        if (dateTime == null) return false
        return dateTime.toLocalDate() == LocalDate.now()
    }
    
    /**
     * Check if date is tomorrow
     */
    fun isTomorrow(dateTime: LocalDateTime?): Boolean {
        if (dateTime == null) return false
        return dateTime.toLocalDate() == LocalDate.now().plusDays(1)
    }
    
    /**
     * Check if date is in the past
     */
    fun isPast(dateTime: LocalDateTime?): Boolean {
        if (dateTime == null) return false
        return dateTime.isBefore(LocalDateTime.now())
    }
    
    /**
     * Get start of day
     */
    fun startOfDay(date: LocalDate = LocalDate.now()): LocalDateTime {
        return date.atStartOfDay()
    }
    
    /**
     * Get end of day
     */
    fun endOfDay(date: LocalDate = LocalDate.now()): LocalDateTime {
        return date.atTime(23, 59, 59, 999_999_999)
    }
    
    /**
     * Get start of week (Monday)
     */
    fun startOfWeek(date: LocalDate = LocalDate.now()): LocalDate {
        return date.minusDays(date.dayOfWeek.value.toLong() - 1)
    }
    
    /**
     * Get end of week (Sunday)
     */
    fun endOfWeek(date: LocalDate = LocalDate.now()): LocalDate {
        return date.plusDays(7L - date.dayOfWeek.value.toLong())
    }
}
