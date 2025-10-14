package com.samkit.swipeassignment.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents a single product as received from the remote API.
 *
 * This data class serves as a Data Transfer Object (DTO) for deserializing the JSON response
 * from the server using Gson. Each property is annotated with `@SerializedName` to map the
 * JSON keys (which use snake_case) to the class's camelCase property names.
 *
 * An instance of this class is a raw representation of the network data and is meant to be
 * immediately mapped to a `Product` domain model upon receipt.
 *
 * @property image The URL of the product's image. It is nullable to handle cases where the API
 * might not provide an image URL.
 * @property price The selling price of the product.
 * @property productName The name of the product as received from the API.
 * @property productType The category or type of the product (e.g., "Product", "Service").
 * @property tax The tax amount applicable to the product.
 */
data class ProductDto(
    @SerializedName("image")
    val image: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("product_name")
    val productName: String,

    @SerializedName("product_type")
    val productType: String,

    @SerializedName("tax")
    val tax: Double
)