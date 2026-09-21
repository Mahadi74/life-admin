package com.lifeadmin.app

import android.app.Application
import com.lifeadmin.app.core.di.AppContainer
import com.lifeadmin.app.core.notifications.NotificationChannels

/**
 * Application class for Life Admin
 * 
 * Initializes:
 * - Dependency injection container
 * - Notification channels
 * - AdMob (if enabled)
 */
class LifeAdminApplication : Application() {
    
    // Manual DI container
    lateinit var appContainer: AppContainer
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize DI container
        appContainer = AppContainer(this)
        
        // Create notification channels
        NotificationChannels.createChannels(this)
        
        // Initialize AdMob
        appContainer.adManager.initialize()
    }
}
