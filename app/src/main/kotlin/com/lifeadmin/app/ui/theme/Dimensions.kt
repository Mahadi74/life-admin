package com.lifeadmin.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Spacing, corner radius, and elevation system
 * 2030-standard spatial design tokens
 */

/**
 * Spacing scale based on 8dp grid system
 */
object Spacing {
    val none = 0.dp
    val xxxs = 2.dp
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
    val xxxl = 64.dp
    val huge = 96.dp
}

/**
 * Corner radius system for consistent rounded corners
 */
object CornerRadius {
    val none = 0.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 28.dp
    val full = 999.dp
}

/**
 * Elevation system for depth hierarchy
 * Material 3 uses surface tint instead of heavy shadows
 */
object Elevation {
    val none = 0.dp
    val minimal = 1.dp
    val low = 2.dp
    val medium = 4.dp
    val high = 8.dp
    val floating = 12.dp
}

/**
 * Common dimensions used throughout the app
 */
object Dimensions {
    // Minimum touch target (accessibility)
    val minTouchTarget = 48.dp
    
    // Card dimensions
    val cardCornerRadius = CornerRadius.lg
    val cardElevation = Elevation.low
    
    // Bottom sheet
    val bottomSheetCornerRadius = CornerRadius.xxl
    
    // FAB
    val fabSize = 56.dp
    val fabExtendedHeight = 56.dp
    val fabCornerRadius = CornerRadius.lg
    
    // Bottom navigation
    val bottomNavHeight = 80.dp
    
    // Status strip (on cards)
    val statusStripWidth = 4.dp
    
    // Icon sizes
    val iconSizeSmall = 16.dp
    val iconSizeMedium = 24.dp
    val iconSizeLarge = 32.dp
    val iconSizeExtraLarge = 48.dp
    
    // Convenient spacing aliases (for backward compatibility)
    val xxxsmall = Spacing.xxxs
    val small = Spacing.sm
    val medium = Spacing.md
    val large = Spacing.lg
}
