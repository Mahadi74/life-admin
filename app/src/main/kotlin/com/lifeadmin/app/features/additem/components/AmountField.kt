package com.lifeadmin.app.features.additem.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lifeadmin.app.ui.theme.Spacing

/**
 * Amount input field with currency selector
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountField(
    amount: String,
    currency: String,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val currencies = listOf("USD", "EUR", "GBP", "JPY", "CNY", "INR", "AUD", "CAD")
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        Text(
            text = "Amount",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = Spacing.xs)
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Amount field
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                placeholder = { Text("0.00") },
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            
            // Currency selector
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.width(100.dp)
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    singleLine = true
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    currencies.forEach { curr ->
                        DropdownMenuItem(
                            text = { Text(curr) },
                            onClick = {
                                onCurrencyChange(curr)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
