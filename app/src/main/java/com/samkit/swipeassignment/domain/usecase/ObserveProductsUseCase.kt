package com.samkit.swipeassignment.domain.usecase

import com.samkit.swipeassignment.domain.model.Product
import com.samkit.swipeassignment.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class ObserveProductsUseCase(
    private val repository: ProductRepository
) {
    operator fun invoke(): Flow<List<Product>> = repository.observeProducts()
}
