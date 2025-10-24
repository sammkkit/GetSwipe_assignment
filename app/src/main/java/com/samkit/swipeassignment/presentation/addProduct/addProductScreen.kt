package com.samkit.swipeassignment.presentation.addProduct

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.min
import coil3.Bitmap
import coil3.compose.SubcomposeAsyncImage
import com.samkit.swipeassignment.presentation.addProduct.component.SuccessAnimationView
import com.samkit.swipeassignment.util.UiState
import com.samkit.swipeassignment.util.uriToFile
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.min
import kotlin.math.roundToInt

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
    var showImage by remember { mutableStateOf(false) }
    var sizeImage by remember { mutableStateOf("500") }
    var urisImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris: List<Uri> ->
            urisImages = uris
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
                    onClick = {
                        showImage = !showImage
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Edit Image")
                }
                Button(
                    onClick = {
                        viewModel.addProduct()
                              },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is UiState.Loading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add Product")
                }
                if (showImage) {
                    AlertDialog(
                        onDismissRequest = { showImage = false },
                    ) {
                        SimpleCropper(
                            imageUri = urisImages[0],
                            onCrop = { croppedFile ->
                                viewModel.changeFirstImage(croppedFile)
                                showImage = false
                            },
                            onCancel = { showImage = false }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun SimpleCropper(
    imageUri: Uri,
    onCrop: (File) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var viewSize by remember { mutableStateOf(IntSize.Zero) }

    var boxLeft by remember { mutableStateOf(100f) }
    var boxTop by remember { mutableStateOf(100f) }
    var boxWidth by remember { mutableStateOf(300f) }
    var boxHeight by remember { mutableStateOf(300f) }

    val minBoxSize = 150f

    var dragOffsetX by remember { mutableStateOf(0f) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(Color.Black)
            .onSizeChanged { viewSize = it }
    ) {
        SubcomposeAsyncImage(
            model = imageUri,
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit
        )
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragOffsetX = offset.x - boxLeft
                            dragOffsetY = offset.y - boxTop
                        },
                        onDrag = { change, _ ->
                            val newLeft = (change.position.x - dragOffsetX)
                                .coerceIn(0f, viewSize.width - boxWidth)
                            val newTop = (change.position.y - dragOffsetY)
                                .coerceIn(0f, viewSize.height - boxHeight)
                            boxLeft = newLeft
                            boxTop = newTop
                        }
                    )
                }
        ) {
            val viewWidth = size.width
            val viewHeight = size.height
            drawRect(Color(0, 0, 0, 120), size = Size(viewWidth, boxTop)) // Top
            drawRect(Color(0, 0, 0, 120), topLeft = Offset(0f, boxTop + boxHeight), size = Size(viewWidth, viewHeight - (boxTop + boxHeight))) // Bottom
            drawRect(Color(0, 0, 0, 120), topLeft = Offset(0f, boxTop), size = Size(boxLeft, boxHeight)) // Left
            drawRect(Color(0, 0, 0, 120), topLeft = Offset(boxLeft + boxWidth, boxTop), size = Size(viewWidth - (boxLeft + boxWidth), boxHeight)) // Right
            drawRect(
                color = Color.White,
                topLeft = Offset(boxLeft, boxTop),
                size = Size(boxWidth, boxHeight),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (boxLeft + boxWidth - 15.dp.toPx()).roundToInt(),
                        (boxTop + boxHeight - 15.dp.toPx()).roundToInt()
                    )
                }
                .size(30.dp)
                .background(Color.White, shape = CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val (dragX, dragY) = dragAmount
                        val newWidth = (boxWidth + dragX).coerceIn(
                            minBoxSize,
                            viewSize.width.toFloat() - boxLeft
                        )
                        val newHeight = (boxHeight + dragY).coerceIn(
                            minBoxSize,
                            viewSize.height.toFloat() - boxTop
                        )
                        boxWidth = newWidth
                        boxHeight = newHeight
                    }
                }
        )
    }

    Spacer(Modifier.height(12.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text("Cancel")
        }
        Button(
            onClick = {
                if (viewSize.width == 0) return@Button
                val file = cropImageFromBox(
                    context,
                    imageUri,
                    boxLeft,
                    boxTop,
                    boxWidth,
                    boxHeight,
                    viewSize.width
                )
                onCrop(file)
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Crop")
        }
    }
}

fun cropImageFromBox(
    context: Context,
    uri: Uri,
    boxLeft: Float,
    boxTop: Float,
    boxWidth: Float,
    boxHeight: Float,
    viewWidth: Int
): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    val viewSize = viewWidth.toFloat()
    val originalWidth = bitmap.width.toFloat()
    val originalHeight = bitmap.height.toFloat()
    val scale = min(viewSize / originalWidth, viewSize / originalHeight)
    val displayedWidth = originalWidth * scale
    val displayedHeight = originalHeight * scale
    val offsetX = (viewSize - displayedWidth) / 2f
    val offsetY = (viewSize - displayedHeight) / 2f

    val inverseScale = (1f/ scale)

    val relativeBoxLeft = boxLeft - offsetX
    val relativeBoxTop = boxTop - offsetY

    var cropX = (relativeBoxLeft * inverseScale).toInt()
    var cropY = (relativeBoxTop * inverseScale).toInt()
    var cropW = (boxWidth * inverseScale).toInt()
    var cropH = (boxHeight * inverseScale).toInt()
    cropX = cropX.coerceIn(0, bitmap.width - 1)
    cropY = cropY.coerceIn(0, bitmap.height - 1)
    cropW = cropW.coerceIn(1, bitmap.width - cropX)
    cropH = cropH.coerceIn(1, bitmap.height - cropY)
    if (cropW <= 0 || cropH <= 0) {
        throw IllegalStateException("Invalid crop dimensions: $cropW x $cropH")
    }
    val cropped = Bitmap.createBitmap(bitmap, cropX, cropY, cropW, cropH)
    if (bitmap != cropped) {
        bitmap.recycle()
    }

    val file = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use {
        cropped.compress(android.graphics.Bitmap.CompressFormat.JPEG, 95, it)
    }
    return file
}