package com.samkit.swipeassignment.presentation.addProduct

import com.samkit.swipeassignment.domain.model.AddProductRequest
import java.io.File

data class AddProductFormState(
    val name: String = "",
    val type: String = "",
    val price: String = "",
    val tax: String = "",
    val images: List<File> = emptyList()
) {
    fun toRequest(): AddProductRequest? {
        val priceDouble = price.toDoubleOrNull()
        val taxDouble = tax.toDoubleOrNull()

        return if (
            name.isNotBlank() &&
            type.isNotBlank() &&
            priceDouble != null &&
            taxDouble != null
        ) {
            AddProductRequest(
                name = name.trim(),
                type = type.trim(),
                price = priceDouble,
                tax = taxDouble,
                images = images
            )
        } else {
            null
        }
    }
}