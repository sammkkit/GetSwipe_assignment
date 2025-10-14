package com.samkit.swipeassignment.data.mapper

import com.samkit.swipeassignment.data.remote.dto.ProductDto
import com.samkit.swipeassignment.domain.model.Product
import java.util.UUID


/**
 * Converts a `ProductDto` (Data Transfer Object from the network) into a `Product` (domain model).
 *
 * This function is responsible for the following transformations:
 * - Generates a new, random `uuid` for each product, as the remote API does not provide a unique identifier.
 * - Safely handles the potentially nullable `image` field, providing an empty string as a default.
 * - Maps the `productName` and `productType` from the DTO to `name` and `type` in the domain model.
 *
 * @receiver The `ProductDto` instance received from the API call.
 * @return A `Product` instance ready for use within the application's domain and UI layers.
 */
fun ProductDto.toDomain(): Product {
    return Product(
        uuid = UUID.randomUUID().toString(),
        imageUrl = image ?: "",
        name = productName,
        type = productType,
        price = price,
        tax = tax
    )
}