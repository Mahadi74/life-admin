package com.lifeadmin.app.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.domain.repository.CategoryRepository
import com.lifeadmin.app.domain.repository.ReminderRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * ViewModel for search screen
 * Handles real-time search with 300ms debouncing
 */
@OptIn(FlowPreview::class)
class SearchViewModel(
    private val reminderRepository: ReminderRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.All)
    private val _selectedCategory = MutableStateFlow<Long?>(null)
    private val _isSearching = MutableStateFlow(false)
    private val _hasSearched = MutableStateFlow(false)
    
    // Combine all state into single UI state
    val uiState: StateFlow<SearchUiState> = combine(
        _searchQuery,
        _selectedFilter,
        _selectedCategory,
        _isSearching,
        _hasSearched,
        categoryRepository.observeAll()
    ) { flows ->
        val query = flows[0] as String
        val filter = flows[1] as SearchFilter
        val categoryId = flows[2] as Long?
        val searching = flows[3] as Boolean
        val searched = flows[4] as Boolean
        @Suppress("UNCHECKED_CAST")
        val categories = flows[5] as List<Category>
        
        SearchUiState(
            searchQuery = query,
            selectedFilter = filter,
            selectedCategory = categoryId?.let { id -> categories.find { it.id == id } },
            results = emptyList(), // Will be populated by search flow
            categories = categories,
            isSearching = searching,
            hasSearched = searched
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )
    
    // Search results with debouncing
    private val searchResults: StateFlow<List<ReminderItem>> = _searchQuery
        .debounce(300) // Wait 300ms after user stops typing
        .onEach { 
            _isSearching.value = true
            if (it.isNotBlank()) {
                _hasSearched.value = true
            }
        }
        .flatMapLatest { query ->
            if (query.isBlank()) {
                // No query - return all based on filter
                getFlowForFilter(_selectedFilter.value, _selectedCategory.value)
            } else {
                // Search with current filter
                searchWithFilter(query, _selectedFilter.value, _selectedCategory.value)
            }
        }
        .onEach { _isSearching.value = false }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Exposed state with results
    val searchUiState: StateFlow<SearchUiState> = combine(
        uiState,
        searchResults
    ) { state, results ->
        state.copy(results = results)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState()
    )
    
    init {
        // Trigger initial load
        viewModelScope.launch {
            _hasSearched.value = false
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    fun onFilterSelected(filter: SearchFilter) {
        _selectedFilter.value = filter
        _selectedCategory.value = null // Reset category when filter changes
        triggerSearch()
    }
    
    fun onCategorySelected(categoryId: Long?) {
        _selectedCategory.value = categoryId
        triggerSearch()
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _hasSearched.value = false
    }
    
    fun clearFilters() {
        _selectedFilter.value = SearchFilter.All
        _selectedCategory.value = null
        triggerSearch()
    }
    
    private fun triggerSearch() {
        // Force re-evaluation by updating query (even if empty)
        val currentQuery = _searchQuery.value
        _searchQuery.value = currentQuery
    }
    
    private fun searchWithFilter(
        query: String,
        filter: SearchFilter,
        categoryId: Long?
    ): Flow<List<ReminderItem>> {
        // If category is selected, search within that category
        if (categoryId != null) {
            return reminderRepository.searchByCategory(query, categoryId)
                .map { results -> filterByDateCriteria(results, filter) }
        }
        
        // Otherwise search based on filter
        return when (filter) {
            SearchFilter.All -> reminderRepository.search(query)
            SearchFilter.Today -> reminderRepository.searchActive(query)
                .map { results -> filterToday(results) }
            SearchFilter.Upcoming -> reminderRepository.searchActive(query)
                .map { results -> filterUpcoming(results) }
            SearchFilter.Overdue -> reminderRepository.searchActive(query)
                .map { results -> filterOverdue(results) }
            SearchFilter.Completed -> reminderRepository.searchCompleted(query)
            SearchFilter.Archived -> reminderRepository.searchArchived(query)
        }
    }
    
    private fun getFlowForFilter(
        filter: SearchFilter,
        categoryId: Long?
    ): Flow<List<ReminderItem>> {
        // If category is selected, filter by category
        if (categoryId != null) {
            return reminderRepository.observeByCategory(categoryId)
                .map { results -> filterByDateCriteria(results, filter) }
        }
        
        // Otherwise use filter-specific queries
        return when (filter) {
            SearchFilter.All -> reminderRepository.observeActive()
            SearchFilter.Today -> reminderRepository.observeToday()
            SearchFilter.Upcoming -> reminderRepository.observeUpcoming()
            SearchFilter.Overdue -> reminderRepository.observeOverdue()
            SearchFilter.Completed -> reminderRepository.observeCompleted()
            SearchFilter.Archived -> reminderRepository.observeArchived()
        }
    }
    
    private fun filterByDateCriteria(
        results: List<ReminderItem>,
        filter: SearchFilter
    ): List<ReminderItem> {
        return when (filter) {
            SearchFilter.Today -> filterToday(results)
            SearchFilter.Upcoming -> filterUpcoming(results)
            SearchFilter.Overdue -> filterOverdue(results)
            SearchFilter.Completed -> results.filter { it.completed }
            SearchFilter.Archived -> results.filter { it.archived }
            SearchFilter.All -> results
        }
    }
    
    private fun filterToday(results: List<ReminderItem>): List<ReminderItem> {
        val today = LocalDate.now()
        return results.filter { item ->
            !item.completed && 
            !item.archived &&
            item.dueDate?.toLocalDate() == today
        }
    }
    
    private fun filterUpcoming(results: List<ReminderItem>): List<ReminderItem> {
        val now = LocalDateTime.now()
        return results.filter { item ->
            !item.completed && 
            !item.archived &&
            item.dueDate != null && 
            item.dueDate.isAfter(now)
        }
    }
    
    private fun filterOverdue(results: List<ReminderItem>): List<ReminderItem> {
        val now = LocalDateTime.now()
        return results.filter { item ->
            !item.completed && 
            !item.archived &&
            item.dueDate != null && 
            item.dueDate.isBefore(now)
        }
    }
}
