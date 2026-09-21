package com.lifeadmin.app.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeadmin.app.domain.model.ImportStrategy
import com.lifeadmin.app.domain.usecase.BackupRestoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for settings screen
 */
class SettingsViewModel(
    private val backupRestoreUseCase: BackupRestoreUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    fun exportBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null, exportSuccess = null) }
            
            val result = backupRestoreUseCase.exportToJson()
            
            result.fold(
                onSuccess = { filePath ->
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            exportSuccess = "Backup saved to:\n$filePath"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            error = "Export failed: ${error.message}"
                        )
                    }
                }
            )
        }
    }
    
    fun prepareImport(filePath: String) {
        viewModelScope.launch {
            // Validate file first
            val result = backupRestoreUseCase.validateBackupFile(filePath)
            
            result.fold(
                onSuccess = { backupData ->
                    _uiState.update {
                        it.copy(
                            showImportDialog = true,
                            backupFilePath = filePath,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = "Invalid backup file: ${error.message}"
                        )
                    }
                }
            )
        }
    }
    
    fun importBackup(strategy: ImportStrategy) {
        val filePath = _uiState.value.backupFilePath ?: return
        
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isImporting = true,
                    showImportDialog = false,
                    error = null,
                    importSuccess = null
                )
            }
            
            val result = backupRestoreUseCase.importFromJson(filePath, strategy)
            
            result.fold(
                onSuccess = { importResult ->
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            importSuccess = "Successfully imported:\n" +
                                    "${importResult.remindersImported} reminders\n" +
                                    "${importResult.categoriesImported} categories"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isImporting = false,
                            error = "Import failed: ${error.message}"
                        )
                    }
                }
            )
        }
    }
    
    fun dismissImportDialog() {
        _uiState.update { it.copy(showImportDialog = false, backupFilePath = null) }
    }
    
    fun clearMessages() {
        _uiState.update {
            it.copy(
                exportSuccess = null,
                importSuccess = null,
                error = null
            )
        }
    }
}
