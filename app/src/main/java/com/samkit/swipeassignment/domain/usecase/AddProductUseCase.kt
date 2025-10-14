package com.samkit.swipeassignment.domain.usecase

import com.samkit.swipeassignment.domain.model.AddProductRequest
import com.samkit.swipeassignment.domain.repository.ProductRepository

class AddProductUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(request: AddProductRequest) {
        return repository.addProduct(request)
    }
}