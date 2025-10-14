package com.samkit.swipeassignment.domain.usecase

import com.samkit.swipeassignment.domain.repository.ProductRepository

class SyncPendingProductsUseCase(
    private val repository: ProductRepository
) {
    suspend operator fun invoke() {
        repository.syncPendingProducts()
    }
}
