package com.lifeadmin.app.features.additem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lifeadmin.app.domain.model.ItemType
import com.lifeadmin.app.domain.model.RecurrenceType
import com.lifeadmin.app.features.additem.components.*
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Bottom sheet for adding/editing reminders
 * Progressive disclosure with context-aware fields
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemSheet(
    viewModel: AddItemViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = androidx.compose.ui.graphics.Color(0xFFF0EFFF)
    ) {
        AddItemContent(
            uiState = uiState,
            onTitleChange = viewModel::onTitleChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onTypeSelected = viewModel::onTypeSelected,
            onCategorySelected = viewModel::onCategorySelected,
            onDateSelected = viewModel::onDateSelected,
            onTimeSelected = viewModel::onTimeSelected,
            onAmountChange = viewModel::onAmountChange,
            onCurrencyChange = viewModel::onCurrencyChange,
            onReminderEnabledChange = viewModel::onReminderEnabledChange,
            onReminderOffsetChange = viewModel::onReminderOffsetChange,
            onRecurrenceChange = viewModel::onRecurrenceChange,
            onSave = { viewModel.save(onSuccess = onDismiss) },
            onCancel = onDismiss
        )
    }
}

@Composable
private fun AddItemContent(
    uiState: AddItemUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeSelected: (ItemType) -> Unit,
    onCategorySelected: (com.lifeadmin.app.domain.model.Category?) -> Unit,
    onDateSelected: (java.time.LocalDate?) -> Unit,
    onTimeSelected: (java.time.LocalTime?) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onReminderOffsetChange: (Int) -> Unit,
    onRecurrenceChange: (RecurrenceType) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = if (uiState.isEditMode) "Edit Reminder" else "New Reminder",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            )
            
            androidx.compose.material3.Surface(
                modifier = androidx.compose.ui.Modifier.size(40.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = androidx.compose.ui.graphics.Color(0xFFF3F4F6)
            ) {
                IconButton(onClick = onCancel) {
                    Icon(
                        Icons.Default.Close, 
                        contentDescription = "Close",
                        tint = androidx.compose.ui.graphics.Color(0xFF6B7280)
                    )
                }
            }
        }
        
        // Title field
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text("Reminder Title") },
            placeholder = { Text("e.g., Netflix subscription") },
            isError = uiState.titleError != null,
            supportingText = uiState.titleError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFFE5E7EB)
            )
        )
        
        // Type selector
        TypeSelector(
            selectedType = uiState.selectedType,
            onTypeSelected = onTypeSelected,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Category selector
        if (uiState.categories.isNotEmpty()) {
            CategorySelector(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Amount field (conditional)
        if (uiState.shouldShowAmount) {
            AmountField(
                amount = uiState.amount,
                currency = uiState.currency,
                onAmountChange = onAmountChange,
                onCurrencyChange = onCurrencyChange,
                error = uiState.amountError,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Date picker
        DatePickerField(
            selectedDate = uiState.selectedDate,
            onDateSelected = onDateSelected,
            error = uiState.dateError,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Time picker (if date is selected)
        if (uiState.selectedDate != null) {
            TimePickerField(
                selectedTime = uiState.selectedTime,
                onTimeSelected = onTimeSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Reminder settings
        if (uiState.selectedDate != null) {
            ReminderSettings(
                enabled = uiState.reminderEnabled,
                offsetMinutes = uiState.reminderOffsetMinutes,
                suggestedOffsets = uiState.getSuggestedReminderOffsets(),
                onEnabledChange = onReminderEnabledChange,
                onOffsetChange = onReminderOffsetChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Recurrence selector
        RecurrenceSelector(
            recurrenceType = uiState.recurrenceType,
            onRecurrenceChange = onRecurrenceChange,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Description field (optional)
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text("Notes (optional)") },
            placeholder = { Text("Add additional details...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFFE5E7EB)
            )
        )
        
        // Error message
        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = androidx.compose.ui.graphics.Color(0xFF6B7280)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    androidx.compose.ui.graphics.Color(0xFFE5E7EB)
                )
            ) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                )
            }
            
            Button(
                onClick = onSave,
                enabled = !uiState.isSaving && uiState.validate(),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFF6366F1),
                    disabledContainerColor = androidx.compose.ui.graphics.Color(0xFFE5E7EB)
                )
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                } else {
                    Text(
                        "Save Reminder",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    )
                }
            }
        }
        
        // Bottom spacing
        Spacer(modifier = Modifier.height(20.dp))
    }
}
