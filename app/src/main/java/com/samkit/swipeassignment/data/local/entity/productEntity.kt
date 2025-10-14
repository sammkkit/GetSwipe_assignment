package com.samkit.swipeassignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Represents a single product record in the local `products` database table.
 *
 * This data class is a Room entity that defines the schema for the products table.
 * Each instance of this class corresponds to a row in the table.
 *
 * @property uuid The unique identifier and primary key for the product. Generated locally.
 * @property imageUrl The URL of the product's image provided by the remote server.
 * @property name The name of the product.
 * @property type The category or type of the product (e.g., "Product", "Service").
 * @property price The selling price of the product.
 * @property tax The tax amount applicable to the product.
 * @property isPendingSync A boolean flag indicating if this product was created offline and
 * needs to be synchronized with the remote server. `true` if it needs to be synced, `false` otherwise.
 * @property imagePath The local file path of the product's image, used specifically for
 * products created offline to facilitate uploading during synchronization. It's `null` for products
 * fetched from the server.
 * @property createdAt A timestamp (in milliseconds) indicating when this record was created.
 * Used for sorting the product list to show the most recent items first.
 */

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val uuid: String,
//    val id: Int = 0,
    val imageUrl: String,
    val name: String,
    val type: String,
    val price: Double,
    val tax: Double,
    val isPendingSync: Boolean = false,
    val imagePath: String? = null,
    val createdAt: Long
)