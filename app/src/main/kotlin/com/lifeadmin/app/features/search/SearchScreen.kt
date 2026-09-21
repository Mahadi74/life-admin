package com.lifeadmin.app.features.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.features.home.components.ReminderCard
import com.lifeadmin.app.navigation.Screen
import com.lifeadmin.app.ui.theme.Dimensions

/**
 * Search screen with real-time search and filters
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {
    val uiState by viewModel.searchUiState.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            SearchTopBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged,
                onClearQuery = viewModel::clearSearch,
                onFilterClick = { showFilterSheet = true },
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            AnimatedVisibility(
                visible = uiState.selectedFilter != SearchFilter.All || uiState.selectedCategory != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ActiveFiltersRow(
                    selectedFilter = uiState.selectedFilter,
                    selectedCategory = uiState.selectedCategory,
                    onClearFilters = viewModel::clearFilters,
                    modifier = Modifier.padding(horizontal = Dimensions.medium)
                )
            }
            
            // Results
            when {
                uiState.isSearching -> {
                    LoadingState()
                }
                !uiState.hasSearched && uiState.searchQuery.isBlank() -> {
                    InitialState()
                }
                uiState.isEmpty -> {
                    EmptySearchState(
                        query = uiState.searchQuery
                    )
                }
                else -> {
                    SearchResults(
                        results = uiState.results,
                        onItemClick = { item ->
                            navController.navigate(Screen.ItemDetails.createRoute(item.id))
                        }
                    )
                }
            }
        }
    }
    
    // Filter bottom sheet
    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = uiState.selectedFilter,
            currentCategory = uiState.selectedCategory,
            categories = uiState.categories,
            onFilterSelected = { filter ->
                viewModel.onFilterSelected(filter)
                showFilterSheet = false
            },
            onCategorySelected = { categoryId ->
                viewModel.onCategorySelected(categoryId)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onFilterClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search reminders...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClearQuery) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close search"
                )
            }
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter"
                )
            }
        }
    )
}

@Composable
private fun ActiveFiltersRow(
    selectedFilter: SearchFilter,
    selectedCategory: Category?,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.small),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (selectedFilter != SearchFilter.All) {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text(selectedFilter.displayName) }
                )
            }
            
            if (selectedCategory != null) {
                FilterChip(
                    selected = true,
                    onClick = {},
                    label = { Text("${selectedCategory.icon} ${selectedCategory.name}") }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            TextButton(onClick = onClearFilters) {
                Text("Clear all")
            }
        }
    }
}

@Composable
private fun SearchResults(
    results: List<com.lifeadmin.app.domain.model.ReminderItem>,
    onItemClick: (com.lifeadmin.app.domain.model.ReminderItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Dimensions.medium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.small)
    ) {
        item {
            Text(
                text = "${results.size} ${if (results.size == 1) "result" else "results"}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Dimensions.small)
            )
        }
        
        items(
            items = results,
            key = { it.id }
        ) { item ->
            ReminderCard(
                reminder = item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun InitialState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.medium)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "Search your reminders",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Search by title, description, or notes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimensions.small),
            modifier = Modifier.padding(Dimensions.large)
        ) {
            Text(
                text = "No results found",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "No reminders match \"$query\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBottomSheet(
    currentFilter: SearchFilter,
    currentCategory: Category?,
    categories: List<Category>,
    onFilterSelected: (SearchFilter) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.medium)
        ) {
            Text(
                text = "Filter by Status",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dimensions.small)
            )
            
            // Status filters
            SearchFilter.entries.forEach { filter ->
                FilterOption(
                    text = filter.displayName,
                    selected = currentFilter == filter && currentCategory == null,
                    onClick = { onFilterSelected(filter) }
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = Dimensions.medium))
            
            Text(
                text = "Filter by Category",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = Dimensions.small)
            )
            
            // Category filters
            FilterOption(
                text = "All Categories",
                selected = currentCategory == null,
                onClick = { onCategorySelected(null) }
            )
            
            categories.forEach { category ->
                FilterOption(
                    text = "${category.icon} ${category.name}",
                    selected = currentCategory?.id == category.id,
                    onClick = { onCategorySelected(category.id) }
                )
            }
            
            Spacer(modifier = Modifier.height(Dimensions.large))
        }
    }
}

@Composable
private fun FilterOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
