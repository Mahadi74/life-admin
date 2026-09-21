package com.lifeadmin.app.core.di

import android.content.Context
import com.lifeadmin.app.core.ads.AdManager
import com.lifeadmin.app.core.database.LifeAdminDatabase
import com.lifeadmin.app.core.notifications.NotificationScheduler
import com.lifeadmin.app.data.repository.CategoryRepositoryImpl
import com.lifeadmin.app.data.repository.ReminderRepositoryImpl
import com.lifeadmin.app.domain.repository.CategoryRepository
import com.lifeadmin.app.domain.repository.ReminderRepository
import com.lifeadmin.app.domain.usecase.BackupRestoreUseCase
import com.lifeadmin.app.domain.usecase.CalculateNextOccurrenceUseCase
import com.lifeadmin.app.domain.usecase.CompleteReminderUseCase

/**
 * Manual Dependency Injection container
 * 
 * Provides singleton instances of:
 * - Database
 * - Repositories
 * - Use cases
 * - Managers (Ads, Notifications, etc.)
 * 
 * Simple, lightweight alternative to Hilt/Dagger for MVP
 */
class AppContainer(private val context: Context) {
    
    // ===== DATABASE =====
    
    val database: LifeAdminDatabase by lazy {
        LifeAdminDatabase.getInstance(context)
    }
    
    // ===== REPOSITORIES =====
    
    val reminderRepository: ReminderRepository by lazy {
        ReminderRepositoryImpl(database.reminderDao())
    }
    
    val categoryRepository: CategoryRepository by lazy {
        CategoryRepositoryImpl(database.categoryDao())
    }
    
    // ===== USE CASES =====
    
    val calculateNextOccurrenceUseCase: CalculateNextOccurrenceUseCase by lazy {
        CalculateNextOccurrenceUseCase()
    }
    
    val completeReminderUseCase: CompleteReminderUseCase by lazy {
        CompleteReminderUseCase(
            reminderRepository = reminderRepository,
            calculateNextOccurrence = calculateNextOccurrenceUseCase
        )
    }
    
    val backupRestoreUseCase: BackupRestoreUseCase by lazy {
        BackupRestoreUseCase(
            context = context,
            reminderRepository = reminderRepository,
            categoryRepository = categoryRepository
        )
    }
    
    // ===== MANAGERS =====
    
    val notificationScheduler: NotificationScheduler by lazy {
        NotificationScheduler(context)
    }
    
    val adManager: AdManager by lazy {
        AdManager(context)
    }
}
