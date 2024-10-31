package com.wahyusembiring.ui.component.floatingactionbutton.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.ui.theme.spacing
import kotlin.math.abs
import kotlin.math.roundToInt


private const val SUB_FAB_COLUMN_ID = "fab_column"
private const val MAIN_FAB_LAYOUT_ID = "main_fab"


@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    mainFloatingActionButton: @Composable () -> Unit,
    subFloatingActionButton: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        MultiFloatingActionButtonLayout(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .layoutId(SUB_FAB_COLUMN_ID)
            ) {
                subFloatingActionButton()
            }
            Box(
                modifier = Modifier
                   .layoutId(MAIN_FAB_LAYOUT_ID)
                   .padding(top = MaterialTheme.spacing.Small)
            ) {
                mainFloatingActionButton()
            }
        }
    }
}

@Composable
private fun MultiFloatingActionButtonLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val subFabColumnMeasurable = measurables.first { it.layoutId == SUB_FAB_COLUMN_ID }
        val mainFabMeasurables = measurables.first { it.layoutId == MAIN_FAB_LAYOUT_ID }
        val subFabColumnPlaceable = subFabColumnMeasurable.measure(constraints)
        val mainFabPlaceables = mainFabMeasurables.measure(constraints)

        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight
        ) {
            val subFabColumnWidth = subFabColumnPlaceable.width
            val mainFabWidth = mainFabPlaceables.width
            val largestWidth = maxOf(subFabColumnWidth, mainFabWidth)
            val widthDifference = abs(subFabColumnWidth - mainFabWidth)
            val xOffset = (widthDifference / 2f).roundToInt()

            subFabColumnPlaceable.placeRelative(
                x = constraints.maxWidth - largestWidth + (if (subFabColumnWidth >= mainFabWidth) 0 else xOffset),
                y = constraints.maxHeight - (mainFabPlaceables.height + subFabColumnPlaceable.height)
            )

            mainFabPlaceables.placeRelative(
                x = constraints.maxWidth - largestWidth + (if (subFabColumnWidth > mainFabWidth) xOffset else 0),
                y = constraints.maxHeight - mainFabPlaceables.height
            )
        }
    }
}

@Composable
fun SubFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isVisible: Boolean,
    icon: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandIn(
            spring(
                Spring.DampingRatioLowBouncy,
                Spring.StiffnessMediumLow
            )
        ),
        exit = shrinkOut() + fadeOut()
    ) {
        FloatingActionButton(
            modifier = modifier
                .scale(0.8f),
            onClick = onClick,
            content = icon
        )
    }
}