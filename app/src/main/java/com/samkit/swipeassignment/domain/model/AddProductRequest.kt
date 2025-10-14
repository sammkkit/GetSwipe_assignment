package com.samkit.swipeassignment.domain.model

import java.io.File

data class AddProductRequest(
    val name: String,
    val type: String,
    val price: Double,
    val tax: Double,
    val images: List<File>? = null
)