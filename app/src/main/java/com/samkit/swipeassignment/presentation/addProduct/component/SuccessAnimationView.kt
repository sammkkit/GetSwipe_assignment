package com.samkit.swipeassignment.presentation.addProduct.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.samkit.swipeassignment.R

@Composable
fun SuccessAnimationView(onAnimationFinished: () -> Unit) {
    // 1. Load the Lottie animation
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.addtocart) // Ensure this filename is correct
    )

    // 2. Control the animation progress
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1 // Play only once
    )

    // 3. Trigger callback when animation finishes
    LaunchedEffect(progress) {
        if (progress == 1f) {
            onAnimationFinished()
        }
    }

    // 4. The UI - just the animation centered
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Give it a fixed height so the sheet doesn't jump in size
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp) // Adjust size as needed
        )
    }
}