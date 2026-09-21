package com.lifeadmin.app.core.notifications

import android.content.Context
import androidx.work.*
import com.lifeadmin.app.domain.model.ReminderItem
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/**
 * Notification scheduler using WorkManager
 * Handles scheduling, rescheduling, and cancellation of reminder notifications
 * 
 * Key features:
 * - Idempotent scheduling (no duplicates)
 * - Survives app restart
 * - Battery-efficient
 */
class NotificationScheduler(private val context: Context) {
    
    /**
     * Schedule a notification for a reminder
     * Idempotent - calling multiple times with same ID will replace previous schedule
     */
    fun schedule(reminder: ReminderItem) {
        if (!reminder.reminderEnabled || reminder.dueDate == null) {
            // No notification needed
            cancel(reminder.id)
            return
        }
        
        val notificationTime = calculateNotificationTime(
            dueDate = reminder.dueDate,
            offsetMinutes = reminder.reminderOffsetMinutes ?: 0
        )
        
        val now = LocalDateTime.now()
        
        if (notificationTime.isBefore(now)) {
            // Notification time has passed, don't schedule
            cancel(reminder.id)
            return
        }
        
        val delayMillis = Duration.between(now, notificationTime).toMillis()
        
        // Create work request
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    NotificationWorker.KEY_REMINDER_ID to reminder.id,
                    NotificationWorker.KEY_REMINDER_TITLE to reminder.title,
                    NotificationWorker.KEY_REMINDER_DESCRIPTION to (reminder.description ?: ""),
                    NotificationWorker.KEY_DUE_DATE to reminder.dueDate.toString()
                )
            )
            .addTag(getWorkTag(reminder.id))
            .build()
        
        // Schedule with unique work name (replaces existing)
        WorkManager.getInstance(context).enqueueUniqueWork(
            getWorkName(reminder.id),
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Cancel a scheduled notification
     */
    fun cancel(reminderId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(getWorkName(reminderId))
    }
    
    /**
     * Reschedule a notification (same as schedule due to idempotency)
     */
    fun reschedule(reminder: ReminderItem) {
        schedule(reminder)
    }
    
    /**
     * Cancel all scheduled notifications
     */
    fun cancelAll() {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG_PREFIX)
    }
    
    /**
     * Reschedule all active reminders (e.g., after device reboot)
     * This should be called by BootReceiver
     */
    suspend fun rescheduleAll(reminders: List<ReminderItem>) {
        reminders.forEach { reminder ->
            schedule(reminder)
        }
    }
    
    /**
     * Calculate when to show notification based on due date and offset
     */
    private fun calculateNotificationTime(
        dueDate: LocalDateTime,
        offsetMinutes: Int
    ): LocalDateTime {
        return dueDate.minusMinutes(offsetMinutes.toLong())
    }
    
    /**
     * Get unique work name for a reminder
     */
    private fun getWorkName(reminderId: Long): String {
        return "${WORK_NAME_PREFIX}_$reminderId"
    }
    
    /**
     * Get work tag for a reminder
     */
    private fun getWorkTag(reminderId: Long): String {
        return "${WORK_TAG_PREFIX}_$reminderId"
    }
    
    companion object {
        private const val WORK_NAME_PREFIX = "reminder_notification"
        private const val WORK_TAG_PREFIX = "reminder_notification"
    }
}
