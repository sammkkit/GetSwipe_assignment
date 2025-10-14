package com.samkit.swipeassignment.data.repository

import android.content.Context
import android.util.Log
import com.samkit.swipeassignment.data.local.dao.ProductDao
import com.samkit.swipeassignment.data.mapper.toDomain
import com.samkit.swipeassignment.data.mapper.toEntity
import com.samkit.swipeassignment.data.remote.api.SwipeApiService
import com.samkit.swipeassignment.domain.model.AddProductRequest
import com.samkit.swipeassignment.domain.model.Product
import com.samkit.swipeassignment.domain.repository.ProductRepository
import com.samkit.swipeassignment.util.NetworkHelper
import com.samkit.swipeassignment.util.SyncScheduler.schedulePendingProductSync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

/**
 * The concrete implementation of the `ProductRepository` interface.
 *
 * This class acts as the Single Source of Truth for all product-related data in the application.
 * It is responsible for orchestrating data flow between the remote API (`SwipeApiService`) and the
 * local database (`ProductDao`), providing a unified and consistent data access layer for the
 * domain use cases.
 *
 * Key responsibilities include:
 * - Fetching products from the network when online, caching them locally, and serving from the
 * local cache when offline.
 * - Providing a reactive stream of products from the database that the UI can observe for real-time updates.
 * - Handling the creation of new products, attempting to post to the API immediately if online,
 * or saving them locally for later synchronization if offline.
 * - Managing the logic for syncing pending offline-created products with the remote server.
 * - Assigning timestamps to products to ensure correct chronological sorting, with newly created
 * items appearing at the top of the list.
 *
 * @property api The Retrofit service for network operations.
 * @property dao The Room DAO for local database operations.
 * @property networkHelper A utility to check for network connectivity.
 * @property context The application context, required for scheduling background sync tasks.
 */
class ProductRepositoryImpl(
    private val api: SwipeApiService,
    private val dao: ProductDao,
    private val networkHelper: NetworkHelper,
    private val context: Context
) : ProductRepository {

    private val TAG = "ProductDebug"

    override suspend fun getProducts(): List<Product> {
        Log.d(TAG, "[Repo] getProducts called.")
        return if (networkHelper.isNetworkAvailable()) {
            Log.d(TAG, "[Repo] Network is available. Fetching from API.")
            try {
                val remoteProducts = api.getProducts().map { it.toDomain() }

                val now = System.currentTimeMillis()
                val entities = remoteProducts.mapIndexed { index, product ->
                    product.toEntity(
                        isPendingSync = false,
                        timestamp = now - index // e.g., now, now-1, now-2...
                    )
                }
                Log.d(TAG, "[Repo] Fetched ${entities.size} products from API. Inserting into DB.")
                dao.deleteAllSyncedProducts()
                // ✅ FIX: `entities` is already a list of ProductEntity. No need to map it again.
                dao.insertAll(entities)
                Log.d(TAG, "[Repo] DB insert complete.")
                // ✅ FIX: Return the correct variable.
                remoteProducts
            } catch (e: Exception) {
                Log.e(TAG, "[Repo] API fetch failed. Falling back to local DB.", e)
                dao.getAllProducts().map { it.toDomain() }
            }
        } else {
            Log.d(TAG, "[Repo] Network unavailable. Fetching from local DB.")
            dao.getAllProducts().map { it.toDomain() }
        }
    }

    override fun observeProducts(): Flow<List<Product>> {
        Log.d(TAG, "[Repo] observeProducts called. Setting up DB flow.")
        return dao.getAllProductsFlow().map { entities ->
            // This log will appear EVERY time the data changes in the products table.
            Log.d(TAG, "[Repo] DB Flow Emitted: ${entities.size} products from Room.")
            entities.map { it.toDomain() }
        }
    }


    override suspend fun addProduct(request: AddProductRequest) {
        Log.d(TAG, "[Repo] addProduct called for: ${request.name}")
        val localImagePath = request.images?.firstOrNull()?.absolutePath
        val localProduct = Product(
            uuid = UUID.randomUUID().toString(),
            imageUrl = localImagePath ?: "",
            name = request.name,
            type = request.type,
            price = request.price,
            tax = request.tax
        )

        // Create the entity with the current timestamp here.
        val entity = localProduct.toEntity(
            isPendingSync = !networkHelper.isNetworkAvailable(),
            timestamp = System.currentTimeMillis()
        )

        if (networkHelper.isNetworkAvailable()) {
            Log.d(TAG, "[Repo] Network available for addProduct. Attempting API call.")
            val files = request.images?.map { file ->
                val body = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("files[]", file.name, body)
            }

            try {
                api.addProduct(
                    productName = request.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    productType = request.type.toRequestBody("text/plain".toMediaTypeOrNull()),
                    price = request.price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    tax = request.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    files = files
                )
                Log.d(TAG, "[Repo] API call SUCCESSFUL for ${request.name}.")

                Log.d(TAG, "[Repo] Inserting product into Room as SYNCED: $localProduct")
                // Insert the entity that is marked as synced.
                dao.insertProduct(entity.copy(isPendingSync = false))
                Log.d(TAG, "[Repo] Inserted SYNCED product into Room complete.")

            } catch (e: Exception) {
                Log.e(TAG, "[Repo] API call FAILED for ${request.name}. Saving as PENDING.", e)
                // Insert the entity which is already marked as pending.
                dao.insertProduct(entity)
                throw e
            }
        } else {
            Log.d(TAG, "[Repo] Network unavailable for addProduct. Saving as PENDING.")
            schedulePendingProductSync(context)
            // Insert the entity which is already marked as pending.
            dao.insertProduct(entity)
        }
    }
    override suspend fun syncPendingProducts() {
        val pending = dao.getPendingSyncProducts()
        for (entity in pending) {
            try {
                val files = entity.imagePath?.let { path ->
                    val file = java.io.File(path)
                    if (file.exists()) {
                        val body = file.asRequestBody("image/*".toMediaTypeOrNull())
                        listOf(
                            MultipartBody.Part.createFormData("files[]", file.name, body)
                        )
                    } else null
                }
                api.addProduct(
                    productName = entity.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    productType = entity.type.toRequestBody("text/plain".toMediaTypeOrNull()),
                    price = entity.price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    tax = entity.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    files = files
                )

                // ✅ Mark as synced
                dao.update(entity.copy(isPendingSync = false))
            } catch (e: Exception) {
                // 🔁 If fails again, keep it pending
            }
        }
    }
}
