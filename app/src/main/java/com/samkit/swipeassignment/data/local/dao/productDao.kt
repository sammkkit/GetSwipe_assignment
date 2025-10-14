package com.samkit.swipeassignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.samkit.swipeassignment.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow


/**
 * Data Access Object (DAO) for the `products` table.
 *
 * This interface defines the contract for interacting with the local product database using Room.
 * It provides methods for creating, reading, updating, and deleting `ProductEntity` objects.
 * Key features include:
 * - A reactive `Flow` (`getAllProductsFlow`) that provides real-time updates from the database,
 * sorted with the newest products first.
 * - Specific queries for handling offline synchronization logic, such as `getPendingSyncProducts`
 * and `deleteAllSyncedProducts`.
 * - Standard suspend functions for performing one-shot database operations within coroutines.
 *
 * @see ProductEntity
 */
@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)
    @Query("SELECT * FROM products WHERE isPendingSync = 1")
    suspend fun getPendingSyncProducts(): List<ProductEntity>
    @Query("DELETE FROM products")
    suspend fun clearAll()
    @Update
    suspend fun update(product: ProductEntity)
    @Query("DELETE FROM products WHERE isPendingSync = 0")
    suspend fun deleteAllSyncedProducts()

}