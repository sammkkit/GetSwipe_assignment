package com.samkit.swipeassignment.domain.repository

import com.samkit.swipeassignment.domain.model.AddProductRequest
import com.samkit.swipeassignment.domain.model.Product
import kotlinx.coroutines.flow.Flow
/**
 * Defines the contract for data operations related to products.
 *
 * This interface serves as the central point of access to product data for the domain layer,
 * following the Repository design pattern. It abstracts the data sources (network, local database)
 * from the use cases, providing a clean API for fetching, observing, and modifying product data.
 *
 * The implementation of this interface is responsible for coordinating between the remote API
 * and the local Room database, as well as handling offline synchronization logic.
 *
 * @see Product
 * @see AddProductRequest
 */
interface ProductRepository {

    /**
     * Performs a one-time fetch of the product list.
     *
     * This function is typically used for the initial data load or a manual refresh.
     * The implementation will attempt to fetch data from the remote API if a network connection
     * is available, update the local cache, and fall back to the local database if offline.
     *
     * @return A list of `Product` domain models.
     */
    suspend fun getProducts(): List<Product>

    /**
     * Observes the product list for real-time updates.
     *
     * This function returns a Kotlin `Flow` that emits a new list of products whenever the
     * underlying data in the local database changes. This is the primary method for keeping
     * the UI reactive and up-to-date.
     *
     * @return A `Flow` that emits a `List<Product>`.
     */
    fun observeProducts(): Flow<List<Product>>

    /**
     * Adds a new product.
     *
     * This function takes an `AddProductRequest` and handles the logic for creating a new product.
     * The implementation will attempt to send the new product to the remote API and, upon success,
     * save it to the local database. If offline, it will save the product locally with a flag
     * indicating that it needs to be synchronized later.
     *
     * @param request The `AddProductRequest` containing the details of the new product.
     */
    suspend fun addProduct(request: AddProductRequest)

    /**
     * Synchronizes any pending products with the remote server.
     *
     * This function is responsible for finding all products that were created while the device
     * was offline (marked as pending sync) and attempting to upload them to the remote API.
     * Upon successful upload, it updates the local records to mark them as synced.
     */
    suspend fun syncPendingProducts()
}
