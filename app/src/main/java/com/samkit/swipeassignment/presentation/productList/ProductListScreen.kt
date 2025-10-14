package com.samkit.swipeassignment.presentation.productList

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.samkit.swipeassignment.R
import com.samkit.swipeassignment.domain.model.Product
import com.samkit.swipeassignment.presentation.productList.components.AnimatedProductItem
import com.samkit.swipeassignment.presentation.productList.components.ChipFilterRow
import com.samkit.swipeassignment.presentation.productList.components.ProductCard
import com.samkit.swipeassignment.presentation.productList.components.SearchBar
import com.samkit.swipeassignment.util.UiState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    onAddProductClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    val listState = rememberLazyListState()

    val productsCount = if (uiState is UiState.Success) {
        (uiState as UiState.Success<List<Product>>).data.size
    } else 0

    // Reset scroll to top on filter or search change
    LaunchedEffect(selectedFilter, productsCount) {
        listState.scrollToItem(0)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddProductClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Product") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 🔍 Search bar
            SearchBar(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🪄 Filter Chips
            ChipFilterRow(
                options = listOf("All", "Product", "Service"),
                selected = selectedFilter,
                onSelected = {
                    selectedFilter = it
                    viewModel.onFilterChanged(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (uiState) {
                is UiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        (uiState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is UiState.Empty -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No products found.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                is UiState.Success -> {
                    val products = (uiState as UiState.Success<List<Product>>).data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = 90.dp // enough space for FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        state = listState
                    ) {
                        items(products, key = { it.uuid }) { product ->
                            AnimatedProductItem(product = product)
                        }
                    }
                }
            }
        }
    }
}






