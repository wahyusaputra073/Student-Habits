package com.wahyusembiring.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Spacing {
    val Large = 24.dp
    val Medium = 16.dp
    val Small = 8.dp
    val ExtraSmall = 4.dp

    val FontLarge = 24.sp
    val FontMedium = 16.sp
    val FontSmall = 8.sp
    val FontExtraSmall = 4.sp
}

val LocalSpacing = compositionLocalOf { Spacing }

val MaterialTheme.spacing: Spacing
    @Composable
    get() = com.wahyusembiring.ui.theme.LocalSpacing.current