package com.lifeadmin.app.core.util

import android.content.Context
import android.database.sqlite.SQLiteException
import com.lifeadmin.app.R
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Centralized error handling utility
 * Converts technical exceptions into user-friendly messages
 */
object ErrorHandler {
    
    /**
     * Convert exception to user-friendly error message
     */
    fun getUserMessage(context: Context, throwable: Throwable): String {
        return when (throwable) {
            // Database errors
            is SQLiteException -> context.getString(R.string.error_database)
            
            // File/IO errors
            is IOException -> context.getString(R.string.error_file_access)
            
            // Network errors (for future features)
            is UnknownHostException,
            is SocketTimeoutException -> context.getString(R.string.error_network)
            
            // Specific app errors
            is BackupFileNotFoundException -> context.getString(R.string.error_backup_not_found)
            is BackupVersionMismatchException -> context.getString(R.string.error_backup_version)
            is InvalidBackupFormatException -> context.getString(R.string.error_backup_invalid)
            is NotificationPermissionDeniedException -> context.getString(R.string.error_notification_permission)
            is StoragePermissionDeniedException -> context.getString(R.string.error_storage_permission)
            
            // Generic error
            else -> throwable.message ?: context.getString(R.string.error_generic)
        }
    }
    
    /**
     * Execute a block with error handling
     * Returns Result with success or user-friendly error message
     */
    suspend fun <T> executeWithErrorHandling(
        context: Context,
        block: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(block())
        } catch (e: Exception) {
            val message = getUserMessage(context, e)
            Result.failure(AppException(message, e))
        }
    }
}

/**
 * Base app exception with user-friendly message
 */
class AppException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)

/**
 * Domain-specific exceptions
 */
class BackupFileNotFoundException(message: String = "Backup file not found") : Exception(message)
class BackupVersionMismatchException(message: String = "Backup version mismatch") : Exception(message)
class InvalidBackupFormatException(message: String = "Invalid backup format") : Exception(message)
class NotificationPermissionDeniedException(message: String = "Notification permission denied") : Exception(message)
class StoragePermissionDeniedException(message: String = "Storage permission denied") : Exception(message)
