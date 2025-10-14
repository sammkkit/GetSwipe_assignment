package com.samkit.swipeassignment.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.samkit.swipeassignment.data.local.dao.ProductDao
import com.samkit.swipeassignment.data.local.entity.ProductEntity


/**
 * The main Room database class for the application.
 *
 * This abstract class extends `RoomDatabase` and serves as the primary access point
 * to the app's persisted, relational data. It is annotated with `@Database` to define
 * the list of entities (database tables) and the database version.
 *
 * The `exportSchema` is set to false to avoid exporting the database schema into a
 * version control system, which is suitable for this project's scope.
 *
 * It provides an abstract method `productDao()` which Room's generated implementation
 * will use to return an instance of the `ProductDao`.
 *
 * @property productDao Provides access to the Data Access Object for the 'products' table.
 * @see ProductEntity
 * @see ProductDao
 */
@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}