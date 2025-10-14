package com.samkit.swipeassignment.presentation.addProduct

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import coil3.compose.SubcomposeAsyncImage
import com.samkit.swipeassignment.presentation.addProduct.component.SuccessAnimationView
import com.samkit.swipeassignment.util.UiState
import com.samkit.swipeassignment.util.uriToFile
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel = koinViewModel(),
    onProductAdded: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currentState = uiState

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris: List<Uri> ->
            val files = uris.mapNotNull { uri -> uriToFile(context, uri) }
            viewModel.updateImages(files)
        }
    )
    if (uiState is UiState.Success) {
        SuccessAnimationView(onAnimationFinished = onProductAdded)
    }else {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Add New Product",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = formState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = formState.type,
                    onValueChange = viewModel::updateType,
                    label = { Text("Product Type") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = formState.price,
                        onValueChange = viewModel::updatePrice,
                        label = { Text("Price") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = formState.tax,
                        onValueChange = viewModel::updateTax,
                        label = { Text("Tax (%)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // 📸 Image Picker Button
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Select Images")
                }

                if (formState.images.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(formState.images) { index, file ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.primaryContainer.copy(
                                                    alpha = 0.25f
                                                ),
                                                MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = 0.25f
                                                )
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                SubcomposeAsyncImage(
                                    model = file,
                                    contentDescription = "Selected image",
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    loading = {
                                        CircularProgressIndicator(
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    },
                                    error = {
                                        Text(
                                            "❌",
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                )

                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.TopEnd)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.removeImageAt(index)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                when (currentState) {
                    is UiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    is UiState.Error -> Text(
                        text = (uiState as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    else -> {}
                }

                Button(
                    onClick = { viewModel.addProduct() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is UiState.Loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Product")
                }
            }
        }
    }
}

