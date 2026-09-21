package com.lifeadmin.app.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lifeadmin.app.LifeAdminApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Boot receiver to reschedule notifications after device restart
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            rescheduleNotifications(context)
        }
    }
    
    private fun rescheduleNotifications(context: Context) {
        val appContainer = (context.applicationContext as LifeAdminApplication).appContainer
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get all active reminders
                val reminders = appContainer.reminderRepository.observeActive()
                
                reminders.collect { activeReminders ->
                    // Reschedule all
                    appContainer.notificationScheduler.rescheduleAll(activeReminders)
                }
            } catch (e: Exception) {
                // Silent failure - notifications will be rescheduled when app opens
            }
        }
    }
}
