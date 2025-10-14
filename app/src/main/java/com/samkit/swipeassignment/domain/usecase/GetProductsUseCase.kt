package com.samkit.swipeassignment.domain.usecase

import com.samkit.swipeassignment.domain.model.Product
import com.samkit.swipeassignment.domain.repository.ProductRepository


class GetProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): List<Product> {
        return repository.getProducts()
    }
}