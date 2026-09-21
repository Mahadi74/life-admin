package com.lifeadmin.app.domain.repository

import com.lifeadmin.app.domain.model.ReminderItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repository interface for reminder operations
 * Abstracts data source from business logic
 */
interface ReminderRepository {
    
    // ===== CREATE =====
    
    suspend fun create(reminder: ReminderItem): Long
    
    suspend fun createAll(reminders: List<ReminderItem>)
    
    // ===== READ =====
    
    suspend fun getById(id: Long): ReminderItem?
    
    fun observeById(id: Long): Flow<ReminderItem?>
    
    fun observeAll(): Flow<List<ReminderItem>>
    
    fun observeActive(): Flow<List<ReminderItem>>
    
    fun observeToday(): Flow<List<ReminderItem>>
    
    fun observeUpcoming(limit: Int = 50): Flow<List<ReminderItem>>
    
    fun observeOverdue(): Flow<List<ReminderItem>>
    
    fun observeByDateRange(start: LocalDate, end: LocalDate): Flow<List<ReminderItem>>
    
    fun observeByCategory(categoryId: Long): Flow<List<ReminderItem>>
    
    fun observeCompleted(limit: Int = 50): Flow<List<ReminderItem>>
    
    fun observeArchived(): Flow<List<ReminderItem>>
    
    // ===== UPDATE =====
    
    suspend fun update(reminder: ReminderItem)
    
    suspend fun complete(id: Long)
    
    suspend fun uncomplete(id: Long)
    
    suspend fun archive(id: Long)
    
    suspend fun unarchive(id: Long)
    
    // ===== DELETE =====
    
    suspend fun delete(id: Long)
    
    suspend fun deleteAll()
    
    // ===== SEARCH =====
    
    fun search(query: String): Flow<List<ReminderItem>>
    
    fun searchActive(query: String): Flow<List<ReminderItem>>
    
    fun searchCompleted(query: String): Flow<List<ReminderItem>>
    
    fun searchArchived(query: String): Flow<List<ReminderItem>>
    
    fun searchByCategory(query: String, categoryId: Long): Flow<List<ReminderItem>>
    
    // ===== STATISTICS =====
    
    fun observeActiveCount(): Flow<Int>
    
    fun observeOverdueCount(): Flow<Int>
}
