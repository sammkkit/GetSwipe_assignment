package com.samkit.swipeassignment.data.mapper
/**
 * This file contains extension functions for mapping between the data layer models (`ProductEntity`)
 * and the domain layer models (`Product`).
 *
 * These mappers are a crucial part of the clean architecture, ensuring that the data layer
 * is completely separated from the domain (business logic) layer.
 */
import com.samkit.swipeassignment.data.local.entity.ProductEntity
import com.samkit.swipeassignment.domain.model.Product
import java.util.UUID

/**
 * Converts a `ProductEntity` (database object) into a `Product` (domain object).
 *
 * This function is used when retrieving data from the local Room database to present it
 * to the upper layers of the application (UI and domain).
 *
 * @receiver The `ProductEntity` instance from the database.
 * @return A `Product` instance suitable for use in the UI and business logic.
 */
fun ProductEntity.toDomain(): Product = Product(
    uuid = uuid,
    imageUrl = imageUrl,
    name = name,
    type = type,
    price = price,
    tax = tax
)

/**
 * Converts a `Product` (domain object) into a `ProductEntity` (database object).
 *
 * This function is used to prepare a domain object for insertion or updating in the
 * local Room database. It handles the logic for offline synchronization and sorting.
 *
 * It's important to note that this function generates a new `uuid` for every conversion,
 * assuming it's for a new product entry. If updating an existing product were required,
 * the `uuid` from the original `Product` model would need to be used.
 *
 * @receiver The `Product` instance from the domain or UI layer.
 * @param isPendingSync A boolean indicating if the product is created offline and needs
 * to be synced with the server later. This determines if the `imagePath` is saved.
 * @param timestamp The creation timestamp (in milliseconds) for the product. This is crucial
 * for sorting the product list correctly.
 * @return A `ProductEntity` instance ready to be saved in the database.
 */
fun Product.toEntity(isPendingSync: Boolean = false,timestamp: Long): ProductEntity = ProductEntity(
    uuid = UUID.randomUUID().toString(),
    imageUrl = imageUrl,
    name = name,
    type = type,
    price = price,
    tax = tax,
    isPendingSync = isPendingSync,
    imagePath = if (isPendingSync) imageUrl else null,
    createdAt = timestamp

)