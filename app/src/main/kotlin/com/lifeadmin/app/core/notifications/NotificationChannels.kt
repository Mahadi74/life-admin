package com.lifeadmin.app.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.lifeadmin.app.R

/**
 * Notification channel management for Life Admin
 * Creates and maintains notification channels for different reminder types
 */
object NotificationChannels {
    
    const val CHANNEL_REMINDERS = "reminders"
    const val CHANNEL_HIGH_PRIORITY = "reminders_high_priority"
    
    /**
     * Create all notification channels
     * Must be called on app startup
     */
    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Default reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                context.getString(R.string.notification_channel_reminders_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_reminders_description)
                enableVibration(true)
                enableLights(true)
            }
            
            // High priority channel for urgent reminders (bills, appointments)
            val highPriorityChannel = NotificationChannel(
                CHANNEL_HIGH_PRIORITY,
                "Urgent Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority reminders for bills and important appointments"
                enableVibration(true)
                enableLights(true)
            }
            
            notificationManager.createNotificationChannels(
                listOf(remindersChannel, highPriorityChannel)
            )
        }
    }
}
