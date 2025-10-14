package com.samkit.swipeassignment.data.remote.api

import com.samkit.swipeassignment.data.remote.dto.ProductDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * A Retrofit service interface that defines the API endpoints for interacting with the Swipe server.
 *
 * This interface outlines the HTTP operations required by the application, such as fetching
 * a list of products and adding a new product. Retrofit uses these definitions to generate
 * the network request implementation at runtime.
 */
interface SwipeApiService {
    /**
     * Fetches the complete list of products from the server.
     *
     * This is a suspend function that performs a GET request to the `public/get` endpoint.
     * It expects a JSON array of product objects in the response, which are deserialized
     * into a list of `ProductDto` objects.
     *
     * @return A list of `ProductDto` representing the products available on the server.
     */
    @GET("public/get")
    suspend fun getProducts(): List<ProductDto>

    /**
     * Adds a new product to the server via a multipart form-data request.
     *
     * This suspend function performs a POST request to the `public/add` endpoint. It is
     * designed to handle both textual data and optional file uploads (images). Each piece of
     * data is sent as a separate part in the multipart request.
     *
     * @param productName The name of the product, wrapped in a `RequestBody`.
     * @param productType The type or category of the product, wrapped in a `RequestBody`.
     * @param price The price of the product, wrapped in a `RequestBody`.
     * @param tax The tax percentage for the product, wrapped in a `RequestBody`.
     * @param files An optional list of `MultipartBody.Part` representing the image files to be uploaded.
     * This can be null if no images are being added.
     */
    @Multipart
    @POST("public/add")
    suspend fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part files: List<MultipartBody.Part>? = null
    )
}