package com.lifeadmin.app.domain.repository

import com.lifeadmin.app.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for category operations
 */
interface CategoryRepository {
    
    suspend fun create(category: Category): Long
    
    suspend fun createAll(categories: List<Category>)
    
    suspend fun getById(id: Long): Category?
    
    fun observeById(id: Long): Flow<Category?>
    
    fun observeAll(): Flow<List<Category>>
    
    suspend fun getAll(): List<Category>
    
    suspend fun update(category: Category)
    
    suspend fun delete(id: Long)
    
    suspend fun getCount(): Int
}
