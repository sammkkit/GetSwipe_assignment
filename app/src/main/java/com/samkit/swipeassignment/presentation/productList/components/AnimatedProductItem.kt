package com.samkit.swipeassignment.presentation.productList.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.samkit.swipeassignment.domain.model.Product
import kotlinx.coroutines.delay

@Composable
fun AnimatedProductItem(product: Product) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(5)
        visible = true
    }

    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f)
    val offsetY by animateFloatAsState(targetValue = if (visible) 0f else 50f)

    ProductCard(
        product = product,
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY
            }
    )
}
