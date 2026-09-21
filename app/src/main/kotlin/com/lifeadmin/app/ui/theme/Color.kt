package com.lifeadmin.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette for Life Admin
 * Modern, soft color scheme inspired by contemporary design
 */

// Light Theme Colors
object LightColors {
    // Primary - Soft Blue/Purple
    val primary = Color(0xFF6366F1)
    val onPrimary = Color(0xFFFFFFFF)
    val primaryContainer = Color(0xFFE0E7FF)
    val onPrimaryContainer = Color(0xFF1E1B4B)
    
    // Secondary - Soft Teal
    val secondary = Color(0xFF14B8A6)
    val onSecondary = Color(0xFFFFFFFF)
    val secondaryContainer = Color(0xFFCCFBF1)
    val onSecondaryContainer = Color(0xFF134E4A)
    
    // Tertiary - Soft Pink/Rose
    val tertiary = Color(0xFFEC4899)
    val onTertiary = Color(0xFFFFFFFF)
    val tertiaryContainer = Color(0xFFFCE7F3)
    val onTertiaryContainer = Color(0xFF831843)
    
    // Error
    val error = Color(0xFFEF4444)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFFFEE2E2)
    val onErrorContainer = Color(0xFF7F1D1D)
    
    // Surface
    val surface = Color(0xFFFFFFFF)
    val onSurface = Color(0xFF18181B)
    val surfaceVariant = Color(0xFFF4F4F5)
    val onSurfaceVariant = Color(0xFF71717A)
    
    // Background
    val background = Color(0xFFFAFAFA)
    val onBackground = Color(0xFF18181B)
    
    // Outline
    val outline = Color(0xFFD4D4D8)
    val outlineVariant = Color(0xFFE4E4E7)
}

// Dark Theme Colors
object DarkColors {
    // Primary
    val primary = Color(0xFF818CF8)
    val onPrimary = Color(0xFF1E1B4B)
    val primaryContainer = Color(0xFF4338CA)
    val onPrimaryContainer = Color(0xFFE0E7FF)
    
    // Secondary
    val secondary = Color(0xFF5EEAD4)
    val onSecondary = Color(0xFF134E4A)
    val secondaryContainer = Color(0xFF0F766E)
    val onSecondaryContainer = Color(0xFFCCFBF1)
    
    // Tertiary
    val tertiary = Color(0xFFF472B6)
    val onTertiary = Color(0xFF831843)
    val tertiaryContainer = Color(0xFF9F1239)
    val onTertiaryContainer = Color(0xFFFCE7F3)
    
    // Error
    val error = Color(0xFFF87171)
    val onError = Color(0xFF7F1D1D)
    val errorContainer = Color(0xFFB91C1C)
    val onErrorContainer = Color(0xFFFEE2E2)
    
    // Surface
    val surface = Color(0xFF1C1C1E)
    val onSurface = Color(0xFFF4F4F5)
    val surfaceVariant = Color(0xFF2C2C2E)
    val onSurfaceVariant = Color(0xFFA1A1AA)
    
    // Background
    val background = Color(0xFF121212)
    val onBackground = Color(0xFFF4F4F5)
    
    // Outline
    val outline = Color(0xFF52525B)
    val outlineVariant = Color(0xFF3F3F46)
}

/**
 * Semantic status colors - soft, modern palette
 */
object StatusColors {
    val overdue = Color(0xFFEF4444)
    val dueToday = Color(0xFFF59E0B)
    val dueSoon = Color(0xFFF97316)
    val upcoming = Color(0xFF14B8A6)
    val completed = Color(0xFF10B981)
    val archived = Color(0xFF9CA3AF)
}
