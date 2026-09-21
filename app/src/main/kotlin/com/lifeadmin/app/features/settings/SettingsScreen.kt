package com.lifeadmin.app.features.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lifeadmin.app.domain.model.ImportStrategy
import com.lifeadmin.app.ui.theme.Dimensions

/**
 * Settings screen with backup/restore and preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // File picker for import
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Get real path from URI
            val filePath = it.path ?: return@let
            viewModel.prepareImport(filePath)
        }
    }
    
    // Share intent for export
    val exportShareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { /* Share completed */ }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = androidx.compose.ui.graphics.Color(0xFFF0EFFF)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color(0xFFF0EFFF)),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Backup & Restore Section
            item {
                SectionHeader(title = "Backup & Restore")
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimensions.small)
                    ) {
                        // Export button
                        SettingsButton(
                            icon = Icons.Default.CloudUpload,
                            title = "Export Backup",
                            subtitle = "Save all reminders to JSON file",
                            onClick = viewModel::exportBackup,
                            isLoading = uiState.isExporting
                        )
                        
                        Divider()
                        
                        // Import button
                        SettingsButton(
                            icon = Icons.Default.CloudDownload,
                            title = "Import Backup",
                            subtitle = "Restore reminders from file",
                            onClick = { importLauncher.launch("application/json") },
                            isLoading = uiState.isImporting
                        )
                    }
                }
            }
            
            // About Section
            item {
                SectionHeader(title = "About")
            }
            
            item {
                SettingsCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Life Admin",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF9CA3AF)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Never forget what matters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = androidx.compose.ui.graphics.Color(0xFF6B7280)
                        )
                    }
                }
            }
            }
        }
    }
    
    // Success/Error messages
    if (uiState.exportSuccess != null) {
        MessageSnackbar(
            message = uiState.exportSuccess!!,
            isError = false,
            onDismiss = viewModel::clearMessages
        )
    }
    
    if (uiState.importSuccess != null) {
        MessageSnackbar(
            message = uiState.importSuccess!!,
            isError = false,
            onDismiss = viewModel::clearMessages
        )
    }
    
    if (uiState.error != null) {
        MessageSnackbar(
            message = uiState.error!!,
            isError = true,
            onDismiss = viewModel::clearMessages
        )
    }
    
    // Import strategy dialog
    if (uiState.showImportDialog) {
        ImportStrategyDialog(
            onMerge = { viewModel.importBackup(ImportStrategy.MERGE) },
            onReplace = { viewModel.importBackup(ImportStrategy.REPLACE) },
            onDismiss = viewModel::dismissImportDialog
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = androidx.compose.ui.graphics.Color(0xFF1F2937),
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = androidx.compose.ui.graphics.Color.White,
        shadowElevation = 2.dp
    ) {
        content()
    }
}

@Composable
private fun SettingsButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier.fillMaxWidth(),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        androidx.compose.ui.graphics.Color(0xFFE0E7FF),
                        androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color(0xFF9CA3AF)
                )
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = androidx.compose.ui.graphics.Color(0xFF6366F1)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
private fun MessageSnackbar(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(if (isError) 5000 else 3000)
        onDismiss()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.medium),
        contentAlignment = Alignment.BottomCenter
    ) {
        Snackbar(
            containerColor = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onPrimaryContainer
            }
        ) {
            Text(message)
        }
    }
}

@Composable
private fun ImportStrategyDialog(
    onMerge: () -> Unit,
    onReplace: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null
            )
        },
        title = {
            Text("Import Strategy")
        },
        text = {
            Column {
                Text("How would you like to import the backup?")
                Spacer(modifier = Modifier.height(Dimensions.medium))
                Text(
                    text = "• Merge: Add to existing reminders",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(Dimensions.xxxsmall))
                Text(
                    text = "• Replace: Delete all and import",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onReplace) {
                Text("Replace All")
            }
        },
        dismissButton = {
            TextButton(onClick = onMerge) {
                Text("Merge")
            }
        }
    )
}
