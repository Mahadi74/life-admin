package com.lifeadmin.app.data.repository

import com.lifeadmin.app.core.database.dao.ReminderDao
import com.lifeadmin.app.data.local.entity.ReminderEntity
import com.lifeadmin.app.domain.model.ReminderItem
import com.lifeadmin.app.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Implementation of ReminderRepository using Room database
 */
class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {
    
    // ===== CREATE =====
    
    override suspend fun create(reminder: ReminderItem): Long {
        val entity = ReminderEntity.fromDomain(reminder)
        return reminderDao.insert(entity)
    }
    
    override suspend fun createAll(reminders: List<ReminderItem>) {
        val entities = reminders.map { ReminderEntity.fromDomain(it) }
        reminderDao.insertAll(entities)
    }
    
    // ===== READ =====
    
    override suspend fun getById(id: Long): ReminderItem? {
        return reminderDao.getById(id)?.toDomain()
    }
    
    override fun observeById(id: Long): Flow<ReminderItem?> {
        return reminderDao.observeById(id).map { it?.toDomain() }
    }
    
    override fun observeAll(): Flow<List<ReminderItem>> {
        return reminderDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeActive(): Flow<List<ReminderItem>> {
        return reminderDao.observeActive().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeToday(): Flow<List<ReminderItem>> {
        val startOfDay = LocalDate.now().atStartOfDay()
        val endOfDay = LocalDate.now().atTime(LocalTime.MAX)
        
        val startMillis = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return reminderDao.observeToday(startMillis, endMillis).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeUpcoming(limit: Int): Flow<List<ReminderItem>> {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return reminderDao.observeUpcoming(now, limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeOverdue(): Flow<List<ReminderItem>> {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return reminderDao.observeOverdue(now).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeByDateRange(start: LocalDate, end: LocalDate): Flow<List<ReminderItem>> {
        val startMillis = start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = end.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        return reminderDao.observeByDateRange(startMillis, endMillis).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeByCategory(categoryId: Long): Flow<List<ReminderItem>> {
        return reminderDao.observeByCategory(categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeCompleted(limit: Int): Flow<List<ReminderItem>> {
        return reminderDao.observeCompleted(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun observeArchived(): Flow<List<ReminderItem>> {
        return reminderDao.observeArchived().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    // ===== UPDATE =====
    
    override suspend fun update(reminder: ReminderItem) {
        val entity = ReminderEntity.fromDomain(reminder)
        reminderDao.update(entity)
    }
    
    override suspend fun complete(id: Long) {
        val now = System.currentTimeMillis()
        reminderDao.updateCompleted(id, completed = true, updatedAt = now)
    }
    
    override suspend fun uncomplete(id: Long) {
        val now = System.currentTimeMillis()
        reminderDao.updateCompleted(id, completed = false, updatedAt = now)
    }
    
    override suspend fun archive(id: Long) {
        val now = System.currentTimeMillis()
        reminderDao.updateArchived(id, archived = true, updatedAt = now)
    }
    
    override suspend fun unarchive(id: Long) {
        val now = System.currentTimeMillis()
        reminderDao.updateArchived(id, archived = false, updatedAt = now)
    }
    
    // ===== DELETE =====
    
    override suspend fun delete(id: Long) {
        reminderDao.deleteById(id)
    }
    
    override suspend fun deleteAll() {
        reminderDao.deleteAll()
    }
    
    // ===== SEARCH =====
    
    override fun search(query: String): Flow<List<ReminderItem>> {
        return reminderDao.search(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchActive(query: String): Flow<List<ReminderItem>> {
        return reminderDao.searchActive(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchCompleted(query: String): Flow<List<ReminderItem>> {
        return reminderDao.searchCompleted(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchArchived(query: String): Flow<List<ReminderItem>> {
        return reminderDao.searchArchived(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override fun searchByCategory(query: String, categoryId: Long): Flow<List<ReminderItem>> {
        return reminderDao.searchByCategory(query, categoryId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    // ===== STATISTICS =====
    
    override fun observeActiveCount(): Flow<Int> {
        return reminderDao.observeActiveCount()
    }
    
    override fun observeOverdueCount(): Flow<Int> {
        val now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return reminderDao.observeOverdueCount(now)
    }
}
