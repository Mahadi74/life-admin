package com.lifeadmin.app.features.itemdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.lifeadmin.app.core.util.CurrencyUtils
import com.lifeadmin.app.core.util.DateUtils
import com.lifeadmin.app.core.util.rememberAppContainer
import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.ui.theme.Spacing
import com.lifeadmin.app.ui.theme.StatusColors
import java.time.format.DateTimeFormatter

/**
 * Item details screen showing complete information about a reminder
 * with actions: complete, snooze, edit, duplicate, archive, delete
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailsScreen(
    itemId: Long,
    navController: NavController
) {
    val appContainer = rememberAppContainer()
    val viewModel = remember {
        ItemDetailsViewModel(
            itemId = itemId,
            reminderRepository = appContainer.reminderRepository,
            categoryRepository = appContainer.categoryRepository,
            completeReminderUseCase = appContainer.completeReminderUseCase,
            calculateNextOccurrence = appContainer.calculateNextOccurrenceUseCase,
            savedStateHandle = SavedStateHandle()
        )
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSnoozeDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Edit action
                    IconButton(onClick = {
                        // TODO: Navigate to edit screen
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    
                    // More actions menu
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Duplicate") },
                                onClick = {
                                    showMenu = false
                                    // TODO: Duplicate functionality
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Archive") },
                                onClick = {
                                    showMenu = false
                                    viewModel.showArchiveConfirmation(true)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Archive, contentDescription = null)
                                }
                            )
                            
                            Divider()
                            
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    showMenu = false
                                    viewModel.showDeleteConfirmation(true)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null)
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.error,
                                    leadingIconColor = MaterialTheme.colorScheme.error
                                )
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        ItemDetailsContent(
            uiState = uiState,
            onComplete = {
                viewModel.complete(onSuccess = { navController.navigateUp() })
            },
            onUncomplete = { viewModel.uncomplete() },
            onSnooze = { showSnoozeDialog = true },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
    
    // Snooze dialog
    if (showSnoozeDialog) {
        SnoozeDialog(
            onDismiss = { showSnoozeDialog = false },
            onSnooze = { days ->
                viewModel.snooze(days)
                showSnoozeDialog = false
            }
        )
    }
    
    // Delete confirmation
    if (uiState.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showDeleteConfirmation(false) },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("Delete Reminder?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(onSuccess = { navController.navigateUp() })
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showDeleteConfirmation(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Archive confirmation
    if (uiState.showArchiveConfirmation) {
        AlertDialog(
            onDismissRequest = { viewModel.showArchiveConfirmation(false) },
            icon = { Icon(Icons.Default.Archive, contentDescription = null) },
            title = { Text("Archive Reminder?") },
            text = { Text("You can restore it later from archived items.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.archive(onSuccess = { navController.navigateUp() })
                    }
                ) {
                    Text("Archive")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showArchiveConfirmation(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ItemDetailsContent(
    uiState: ItemDetailsUiState,
    onComplete: () -> Unit,
    onUncomplete: () -> Unit,
    onSnooze: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        uiState.error != null -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.error,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
        
        uiState.item != null -> {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // Type badge
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${uiState.item.type.emoji} ${uiState.item.type.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = Spacing.sm, vertical = Spacing.xxs)
                    )
                }
                
                // Title
                Text(
                    text = uiState.item.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                
                // Countdown card
                if (uiState.item.dueDate != null && !uiState.item.completed) {
                    CountdownCard(
                        countdownText = uiState.getCountdownText(),
                        dueDate = uiState.item.dueDate,
                        isOverdue = uiState.item.isOverdue()
                    )
                }
                
                // Primary actions
                if (!uiState.item.completed) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onComplete,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text("Complete")
                        }
                        
                        if (uiState.item.dueDate != null) {
                            OutlinedButton(
                                onClick = onSnooze,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Snooze, contentDescription = null)
                                Spacer(modifier = Modifier.width(Spacing.xs))
                                Text("Snooze")
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onUncomplete,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mark as Incomplete")
                    }
                }
                
                Divider()
                
                // Details section
                Text(
                    text = "DETAILS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Amount
                if (uiState.item.amount != null) {
                    DetailRow(
                        label = "Amount",
                        value = CurrencyUtils.formatAmount(uiState.item.amount, uiState.item.currency),
                        icon = Icons.Default.AttachMoney
                    )
                }
                
                // Category
                if (uiState.category != null) {
                    DetailRow(
                        label = "Category",
                        value = "${uiState.category.icon} ${uiState.category.name}",
                        icon = Icons.Default.Category
                    )
                }
                
                // Due date
                if (uiState.item.dueDate != null) {
                    DetailRow(
                        label = "Due Date",
                        value = DateUtils.formatDateTimeForDisplay(uiState.item.dueDate),
                        icon = Icons.Default.Event
                    )
                }
                
                // Reminder
                if (uiState.item.reminderEnabled) {
                    val reminderTime = uiState.item.dueDate?.minusMinutes(
                        (uiState.item.reminderOffsetMinutes ?: 0).toLong()
                    )
                    DetailRow(
                        label = "Reminder",
                        value = DateUtils.formatDateTimeForDisplay(reminderTime),
                        icon = Icons.Default.Notifications
                    )
                }
                
                // Recurrence
                if (uiState.item.recurrenceType !is RecurrenceType.None) {
                    val recurrenceText = when (val rec = uiState.item.recurrenceType) {
                        is RecurrenceType.Daily -> "Every ${rec.interval} day(s)"
                        is RecurrenceType.Weekly -> "Every ${rec.interval} week(s)"
                        is RecurrenceType.Monthly -> "Every ${rec.interval} month(s)"
                        is RecurrenceType.Yearly -> "Every ${rec.interval} year(s)"
                        else -> "None"
                    }
                    
                    DetailRow(
                        label = "Recurring",
                        value = recurrenceText,
                        icon = Icons.Default.Loop
                    )
                    
                    // Show next occurrences
                    if (uiState.nextOccurrences.size > 1) {
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Text(
                            text = "Next occurrences:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 40.dp)
                        )
                        uiState.nextOccurrences.drop(1).take(3).forEach { date ->
                            Text(
                                text = "• ${date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 48.dp)
                            )
                        }
                    }
                }
                
                // Description/Notes
                if (!uiState.item.description.isNullOrBlank()) {
                    Divider()
                    Text(
                        text = "NOTES",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = uiState.item.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                // Metadata
                Divider()
                Text(
                    text = "Created ${DateUtils.formatDateForDisplay(uiState.item.createdAt.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CountdownCard(
    countdownText: String?,
    dueDate: java.time.LocalDateTime,
    isOverdue: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) 
                StatusColors.overdue.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = countdownText ?: "",
                style = MaterialTheme.typography.titleLarge,
                color = if (isOverdue) StatusColors.overdue else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = dueDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' h:mm a")),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SnoozeDialog(
    onDismiss: () -> Unit,
    onSnooze: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Snooze, contentDescription = null) },
        title = { Text("Snooze Reminder") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text("Postpone this reminder:")
                
                TextButton(onClick = { onSnooze(1) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Tomorrow", modifier = Modifier.fillMaxWidth())
                }
                TextButton(onClick = { onSnooze(3) }, modifier = Modifier.fillMaxWidth()) {
                    Text("In 3 days", modifier = Modifier.fillMaxWidth())
                }
                TextButton(onClick = { onSnooze(7) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Next week", modifier = Modifier.fillMaxWidth())
                }
                TextButton(onClick = { onSnooze(30) }, modifier = Modifier.fillMaxWidth()) {
                    Text("In 1 month", modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
