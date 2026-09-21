package com.lifeadmin.app.core.database.dao

import androidx.room.*
import com.lifeadmin.app.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for reminders
 * Provides reactive queries using Flow for real-time UI updates
 */
@Dao
interface ReminderDao {
    
    // ===== INSERT =====
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>)
    
    // ===== UPDATE =====
    
    @Update
    suspend fun update(reminder: ReminderEntity)
    
    @Query("UPDATE reminders SET completed = :completed, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean, updatedAt: Long)
    
    @Query("UPDATE reminders SET archived = :archived, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateArchived(id: Long, archived: Boolean, updatedAt: Long)
    
    // ===== DELETE =====
    
    @Delete
    suspend fun delete(reminder: ReminderEntity)
    
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM reminders")
    suspend fun deleteAll()
    
    // ===== QUERY - Single Item =====
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): ReminderEntity?
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    fun observeById(id: Long): Flow<ReminderEntity?>
    
    // ===== QUERY - All Active =====
    
    @Query("SELECT * FROM reminders WHERE archived = 0 ORDER BY due_date ASC")
    fun observeAll(): Flow<List<ReminderEntity>>
    
    @Query("SELECT * FROM reminders WHERE completed = 0 AND archived = 0 ORDER BY due_date ASC")
    fun observeActive(): Flow<List<ReminderEntity>>
    
    // ===== QUERY - Today =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE completed = 0 
        AND archived = 0 
        AND due_date IS NOT NULL
        AND due_date >= :startOfDay 
        AND due_date < :endOfDay
        ORDER BY due_date ASC
    """)
    fun observeToday(startOfDay: Long, endOfDay: Long): Flow<List<ReminderEntity>>
    
    // ===== QUERY - Upcoming =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE completed = 0 
        AND archived = 0 
        AND due_date IS NOT NULL
        AND due_date >= :after
        ORDER BY due_date ASC
        LIMIT :limit
    """)
    fun observeUpcoming(after: Long, limit: Int = 50): Flow<List<ReminderEntity>>
    
    // ===== QUERY - Overdue =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE completed = 0 
        AND archived = 0 
        AND due_date IS NOT NULL
        AND due_date < :now
        ORDER BY due_date ASC
    """)
    fun observeOverdue(now: Long): Flow<List<ReminderEntity>>
    
    // ===== QUERY - By Date Range =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE completed = 0 
        AND archived = 0 
        AND due_date IS NOT NULL
        AND due_date >= :start 
        AND due_date < :end
        ORDER BY due_date ASC
    """)
    fun observeByDateRange(start: Long, end: Long): Flow<List<ReminderEntity>>
    
    // ===== QUERY - By Category =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE category_id = :categoryId 
        AND archived = 0
        ORDER BY due_date ASC
    """)
    fun observeByCategory(categoryId: Long): Flow<List<ReminderEntity>>
    
    // ===== QUERY - Completed =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE completed = 1 
        AND archived = 0
        ORDER BY updated_at DESC
        LIMIT :limit
    """)
    fun observeCompleted(limit: Int = 50): Flow<List<ReminderEntity>>
    
    // ===== QUERY - Archived =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE archived = 1
        ORDER BY updated_at DESC
    """)
    fun observeArchived(): Flow<List<ReminderEntity>>
    
    // ===== SEARCH =====
    
    @Query("""
        SELECT * FROM reminders 
        WHERE archived = 0
        AND (
            title LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
        )
        ORDER BY 
            CASE WHEN completed = 0 THEN 0 ELSE 1 END,
            due_date ASC
    """)
    fun search(query: String): Flow<List<ReminderEntity>>
    
    @Query("""
        SELECT * FROM reminders 
        WHERE (
            title LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
        )
        AND completed = 0
        AND archived = 0
        ORDER BY due_date ASC
    """)
    fun searchActive(query: String): Flow<List<ReminderEntity>>
    
    @Query("""
        SELECT * FROM reminders 
        WHERE (
            title LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
        )
        AND completed = 1
        AND archived = 0
        ORDER BY updated_at DESC
    """)
    fun searchCompleted(query: String): Flow<List<ReminderEntity>>
    
    @Query("""
        SELECT * FROM reminders 
        WHERE (
            title LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
        )
        AND archived = 1
        ORDER BY updated_at DESC
    """)
    fun searchArchived(query: String): Flow<List<ReminderEntity>>
    
    @Query("""
        SELECT * FROM reminders 
        WHERE (
            title LIKE '%' || :query || '%' 
            OR description LIKE '%' || :query || '%'
            OR notes LIKE '%' || :query || '%'
        )
        AND category_id = :categoryId
        AND archived = 0
        ORDER BY 
            CASE WHEN completed = 0 THEN 0 ELSE 1 END,
            due_date ASC
    """)
    fun searchByCategory(query: String, categoryId: Long): Flow<List<ReminderEntity>>
    
    // ===== STATISTICS =====
    
    @Query("SELECT COUNT(*) FROM reminders WHERE completed = 0 AND archived = 0")
    fun observeActiveCount(): Flow<Int>
    
    @Query("""
        SELECT COUNT(*) FROM reminders 
        WHERE completed = 0 
        AND archived = 0 
        AND due_date IS NOT NULL
        AND due_date < :now
    """)
    fun observeOverdueCount(now: Long): Flow<Int>
}
