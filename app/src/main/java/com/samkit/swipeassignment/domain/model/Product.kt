package com.samkit.swipeassignment.domain.model

/**
 * Represents a single product item.
 * This is now our core domain model, located in the domain.model package.
 */
data class Product(
    val uuid: String,
    val imageUrl: String,
    val name: String,
    val type: String,
    val price: Double,
    val tax: Double
)