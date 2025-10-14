package com.samkit.swipeassignment.presentation.productList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samkit.swipeassignment.domain.model.Product
import com.samkit.swipeassignment.domain.usecase.GetProductsUseCase
import com.samkit.swipeassignment.domain.usecase.ObserveProductsUseCase
import com.samkit.swipeassignment.util.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ProductListViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val observeProductsUseCase: ObserveProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Product>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter = _selectedFilter.asStateFlow()

    private val TAG = "ProductDebug"

    init {
        Log.d(TAG, "[ViewModel] init block started.")
        observeAndFetchProducts()
    }

    private fun observeAndFetchProducts() {
        viewModelScope.launch {
            Log.d(TAG, "[ViewModel] observeAndFetchProducts coroutine launched.")

            combine(
                observeProductsUseCase(),
                _searchQuery.debounce(300).distinctUntilChanged(),
                _selectedFilter
            ) { products, query, filter ->
                Log.d(TAG, "[ViewModel] COMBINE: DB emitted ${products.size} | query='$query' | filter='$filter'")

                products.filter { product ->
                    val matchesSearch = if (query.isNotBlank()) {
                        product.name.contains(query, ignoreCase = true) ||
                                product.type.contains(query, ignoreCase = true)
                    } else true

                    val matchesFilter = when (filter) {
                        "All" -> true
                        else -> product.type.equals(filter, ignoreCase = true)
                    }

                    matchesSearch && matchesFilter
                }
            }
                .onStart {
                    Log.d(TAG, "[ViewModel] ON_START: Triggering initial product fetch from network.")
                    _uiState.value = UiState.Loading
                    try {
                        getProductsUseCase()
                    } catch (e: Exception) {
                        Log.e(TAG, "[ViewModel] ON_START: Error during initial fetch.", e)
                        _uiState.value = UiState.Error(e.localizedMessage ?: "Failed to fetch data")
                    }
                }
                .catch { e ->
                    Log.e(TAG, "[ViewModel] CATCH: Flow collection error.", e)
                    _uiState.value = UiState.Error(e.localizedMessage ?: "An error occurred")
                }
                .collectLatest { filteredProducts ->
                    Log.d(TAG, "[ViewModel] COLLECT: ${filteredProducts.size} products after filter.")
                    _uiState.value = if (filteredProducts.isEmpty() && _searchQuery.value.isBlank()) {
                        UiState.Empty
                    } else {
                        UiState.Success(filteredProducts)
                    }
                }
        }
    }

    fun getProducts() {
        Log.d(TAG, "[ViewModel] getProducts called.")
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val products = getProductsUseCase()
                _uiState.value = if (products.isEmpty()) UiState.Empty else UiState.Success(products)
            } catch (e: Exception) {
                Log.e(TAG, "[ViewModel] GETTER: Error fetching products.", e)
                _uiState.value = UiState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        Log.d(TAG, "[ViewModel] Search query changed to: $query")
        _searchQuery.value = query
    }

    fun onFilterChanged(filter: String) {
        Log.d(TAG, "[ViewModel] Filter changed to: $filter")
        _selectedFilter.value = filter
    }
}
