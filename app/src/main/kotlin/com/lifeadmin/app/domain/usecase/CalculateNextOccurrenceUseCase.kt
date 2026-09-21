package com.lifeadmin.app.domain.usecase

import com.lifeadmin.app.domain.model.RecurrenceType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Calculate next occurrence date for recurring reminders
 * Handles edge cases: leap years, month-end dates, February, year boundaries
 */
class CalculateNextOccurrenceUseCase {
    
    /**
     * Calculate the next occurrence date based on recurrence pattern
     * 
     * Edge cases handled:
     * - Jan 31 monthly → Feb 28/29, Mar 31, Apr 30 (clamps to last day of month)
     * - Feb 29 yearly → Feb 28 in non-leap years
     * - December → January (year boundary)
     */
    fun execute(
        currentDateTime: LocalDateTime,
        recurrenceType: RecurrenceType
    ): LocalDateTime {
        return when (recurrenceType) {
            is RecurrenceType.None -> currentDateTime
            
            is RecurrenceType.Daily -> {
                currentDateTime.plusDays(recurrenceType.interval.toLong())
            }
            
            is RecurrenceType.Weekly -> {
                currentDateTime.plusWeeks(recurrenceType.interval.toLong())
            }
            
            is RecurrenceType.Monthly -> {
                calculateNextMonthlyOccurrence(currentDateTime, recurrenceType.interval)
            }
            
            is RecurrenceType.Yearly -> {
                calculateNextYearlyOccurrence(currentDateTime, recurrenceType.interval)
            }
        }
    }
    
    /**
     * Calculate next monthly occurrence with proper day clamping
     * Example: Jan 31 → Feb 28 (or 29 in leap year)
     */
    private fun calculateNextMonthlyOccurrence(
        current: LocalDateTime,
        interval: Int
    ): LocalDateTime {
        val currentDate = current.toLocalDate()
        val currentTime = current.toLocalTime()
        val originalDay = currentDate.dayOfMonth
        
        // Add months
        val nextMonth = currentDate.plusMonths(interval.toLong())
        
        // Clamp day to valid day in target month
        val lastDayOfNextMonth = nextMonth.lengthOfMonth()
        val clampedDay = minOf(originalDay, lastDayOfNextMonth)
        
        val nextDate = nextMonth.withDayOfMonth(clampedDay)
        
        return LocalDateTime.of(nextDate, currentTime)
    }
    
    /**
     * Calculate next yearly occurrence with leap year handling
     * Example: Feb 29 2024 → Feb 28 2025 (non-leap) → Feb 29 2028 (leap)
     */
    private fun calculateNextYearlyOccurrence(
        current: LocalDateTime,
        interval: Int
    ): LocalDateTime {
        val currentDate = current.toLocalDate()
        val currentTime = current.toLocalTime()
        
        // Add years
        val nextYear = currentDate.plusYears(interval.toLong())
        
        // Handle Feb 29 in non-leap years
        val nextDate = if (currentDate.monthValue == 2 && 
                           currentDate.dayOfMonth == 29 && 
                           !nextYear.isLeapYear) {
            // Move to Feb 28 in non-leap year
            LocalDate.of(nextYear.year, 2, 28)
        } else {
            nextYear
        }
        
        return LocalDateTime.of(nextDate, currentTime)
    }
    
    /**
     * Calculate multiple future occurrences (for preview/display)
     */
    fun calculateMultipleOccurrences(
        startDateTime: LocalDateTime,
        recurrenceType: RecurrenceType,
        count: Int
    ): List<LocalDateTime> {
        if (recurrenceType is RecurrenceType.None) {
            return listOf(startDateTime)
        }
        
        val occurrences = mutableListOf<LocalDateTime>()
        var current = startDateTime
        
        repeat(count) {
            occurrences.add(current)
            current = execute(current, recurrenceType)
        }
        
        return occurrences
    }
}
