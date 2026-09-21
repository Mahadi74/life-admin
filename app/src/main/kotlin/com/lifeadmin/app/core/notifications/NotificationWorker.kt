package com.lifeadmin.app.core.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lifeadmin.app.MainActivity
import com.lifeadmin.app.R

/**
 * WorkManager worker that displays reminder notifications
 */
class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1)
        if (reminderId == -1L) {
            return Result.failure()
        }
        
        val title = inputData.getString(KEY_REMINDER_TITLE) ?: "Reminder"
        val description = inputData.getString(KEY_REMINDER_DESCRIPTION) ?: ""
        val dueDate = inputData.getString(KEY_DUE_DATE) ?: ""
        
        showNotification(
            reminderId = reminderId,
            title = title,
            description = description,
            dueDate = dueDate
        )
        
        return Result.success()
    }
    
    private fun showNotification(
        reminderId: Long,
        title: String,
        description: String,
        dueDate: String
    ) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create intent to open app
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("reminderId", reminderId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(if (description.isNotBlank()) description else "Due soon")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        // Show notification
        notificationManager.notify(reminderId.toInt(), notification)
    }
    
    companion object {
        const val KEY_REMINDER_ID = "reminder_id"
        const val KEY_REMINDER_TITLE = "reminder_title"
        const val KEY_REMINDER_DESCRIPTION = "reminder_description"
        const val KEY_DUE_DATE = "due_date"
    }
}
