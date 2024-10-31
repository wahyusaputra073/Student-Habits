package com.wahyusembiring.ui.component.floatingactionbutton.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wahyusembiring.ui.theme.spacing

const val SCRIM_DIM_OPACITY = 0.4f

@Composable
fun Scrim(
    isVisible: Boolean = false
) {
    val opacityAnimation by animateFloatAsState(
        label = "Scrim Opacity Animation",
        targetValue = if (isVisible) SCRIM_DIM_OPACITY else 0f
    )
    Box(
        modifier = Modifier
            .offset(
                x = MaterialTheme.spacing.Medium,
                y = MaterialTheme.spacing.Medium
            )
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = opacityAnimation))
    )
}