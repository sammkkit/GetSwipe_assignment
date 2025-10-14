package com.samkit.swipeassignment.presentation.addProduct

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.swipeassignment.domain.usecase.AddProductUseCase
import com.samkit.swipeassignment.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _formState = MutableStateFlow(AddProductFormState())
    val formState = _formState.asStateFlow()
    private val TAG = "ProductDebug"
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun updateName(name: String) {
        _formState.value = _formState.value.copy(name = name)
    }

    fun updateType(type: String) {
        _formState.value = _formState.value.copy(type = type)
    }

    fun updatePrice(price: String) {
        _formState.value = _formState.value.copy(price = price)
    }

    fun updateTax(tax: String) {
        _formState.value = _formState.value.copy(tax = tax)
    }

    fun updateImages(images: List<java.io.File>) {
        _formState.value = _formState.value.copy(images = images)
    }

    fun resetState() {
        _uiState.value = UiState.Empty
    }
    fun addProduct() {
        Log.d(TAG, "[AddVM] addProduct button clicked.")
        val request = _formState.value.toRequest()
        if (request == null) {
            _uiState.value = UiState.Error("All fields must be filled and valid")
            Log.w(TAG, "[AddVM] Form validation failed.")
            return
        }

        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                Log.d(TAG, "[AddVM] Calling addProductUseCase with: ${request.name}")
                addProductUseCase(request)
                _uiState.value = UiState.Success(Unit)
                _formState.value = AddProductFormState()
                Log.d(TAG, "[AddVM] addProductUseCase SUCCESS.")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to add product")
                Log.e(TAG, "[AddVM] addProductUseCase FAILED.", e)
            }
        }
    }
    fun removeImageAt(index: Int) {
        val updatedList = _formState.value.images.toMutableList()
        if (index in updatedList.indices) {
            updatedList.removeAt(index)
            _formState.value = _formState.value.copy(images = updatedList)
        }
    }

}