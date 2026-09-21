package com.lifeadmin.app.data.repository

import com.lifeadmin.app.core.database.dao.CategoryDao
import com.lifeadmin.app.data.local.entity.CategoryEntity
import com.lifeadmin.app.domain.model.Category
import com.lifeadmin.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of CategoryRepository using Room database
 */
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {
    
    override suspend fun create(category: Category): Long {
        val entity = CategoryEntity.fromDomain(category)
        return categoryDao.insert(entity)
    }
    
    override suspend fun createAll(categories: List<Category>) {
        val entities = categories.map { CategoryEntity.fromDomain(it) }
        categoryDao.insertAll(entities)
    }
    
    override suspend fun getById(id: Long): Category? {
        return categoryDao.getById(id)?.toDomain()
    }
    
    override fun observeById(id: Long): Flow<Category?> {
        return categoryDao.observeById(id).map { it?.toDomain() }
    }
    
    override fun observeAll(): Flow<List<Category>> {
        return categoryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getAll(): List<Category> {
        return categoryDao.getAll().map { it.toDomain() }
    }
    
    override suspend fun update(category: Category) {
        val entity = CategoryEntity.fromDomain(category)
        categoryDao.update(entity)
    }
    
    override suspend fun delete(id: Long) {
        categoryDao.deleteById(id)
    }
    
    override suspend fun getCount(): Int {
        return categoryDao.getCount()
    }
}
