package com.lifeadmin.app.core.util

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.lifeadmin.app.LifeAdminApplication
import com.lifeadmin.app.core.di.AppContainer

/**
 * Utility extensions and helper functions
 */

/**
 * Get the AppContainer from any composable
 */
@Composable
fun rememberAppContainer(): AppContainer {
    val context = LocalContext.current
    return (context.applicationContext as LifeAdminApplication).appContainer
}

/**
 * Get the AppContainer from an Activity
 */
fun Activity.getAppContainer(): AppContainer {
    return (application as LifeAdminApplication).appContainer
}
