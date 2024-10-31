package com.wahyusembiring.ui.component.popup.picker.colorpicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.component.modalbottomsheet.component.CloseAndSaveHeaderDefaults
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPicker(
    initialColor: Color,
    onDismissRequest: () -> Unit,
    onColorConfirmed: (color: Color) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentSelectedColor by remember { mutableStateOf(initialColor) }
    val colorPickerController = rememberColorPickerController()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        NavigationAndActionButtonHeader(
            onNavigationButtonClicked = {
                coroutineScope.launch { sheetState.hide() }
                    .invokeOnCompletion { onDismissRequest() }
            },
            onActionButtonClicked = {
                onColorConfirmed(currentSelectedColor)
                coroutineScope.launch { sheetState.hide() }
                    .invokeOnCompletion { onDismissRequest() }
            },
            navigationButtonDescription = stringResource(R.string.close_color_picker),
            colors = CloseAndSaveHeaderDefaults.colors(
                closeButtonColor = currentSelectedColor,
                saveButtonContainerColor = currentSelectedColor,
            )
        )
        Column(
            modifier = Modifier
                .padding(MaterialTheme.spacing.Large)
        ) {
            HsvColorPicker(
                modifier = Modifier
                   .fillMaxWidth()
                   .height(300.dp),
                controller = colorPickerController,
                initialColor = initialColor,
                onColorChanged = { currentSelectedColor = it.color }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            BrightnessSlider(
                modifier = Modifier
                   .fillMaxWidth()
                   .height(35.dp),
                controller = colorPickerController,
                initialColor = initialColor
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ColorPickerModalBottomSheetPreview() {
    HabitTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ColorPicker(
                onDismissRequest = { },
                onColorConfirmed = { },
                initialColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}