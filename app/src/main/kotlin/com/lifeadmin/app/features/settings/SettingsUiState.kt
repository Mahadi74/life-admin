package com.lifeadmin.app.features.settings

/**
 * UI state for settings screen
 */
data class SettingsUiState(
    val isExporting: Boolean = false,
    val isImporting: Boolean = false,
    val exportSuccess: String? = null,
    val importSuccess: String? = null,
    val error: String? = null,
    val showImportDialog: Boolean = false,
    val backupFilePath: String? = null
)
