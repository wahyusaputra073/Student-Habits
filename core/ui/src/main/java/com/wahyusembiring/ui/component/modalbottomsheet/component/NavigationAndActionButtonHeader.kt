package com.wahyusembiring.ui.component.modalbottomsheet.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun NavigationAndActionButtonHeader(
    modifier: Modifier = Modifier,
    onNavigationButtonClicked: () -> Unit,
    @DrawableRes navigationButtonIcon: Int = R.drawable.ic_close,
    onActionButtonClicked: () -> Unit,
    actionButtonText: String = stringResource(R.string.save),
    navigationButtonDescription: String? = null,
    colors: CloseAndSaveHeaderColors = CloseAndSaveHeaderDefaults.colors()
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigationButtonClicked) {
            Icon(
                painter = painterResource(id = navigationButtonIcon),
                contentDescription = navigationButtonDescription,
                tint = colors.closeButtonColor
            )
        }
        Button(
            modifier = Modifier.padding(end = MaterialTheme.spacing.Medium),
            colors = colors.saveButtonColor,
            onClick = onActionButtonClicked
        ) {
            Text(text = actionButtonText)
        }
    }
}

object CloseAndSaveHeaderDefaults {
    @Composable
    fun colors(
        closeButtonColor: Color = LocalContentColor.current,
        saveButtonContainerColor: Color = ButtonDefaults.buttonColors().containerColor,
        saveButtonContentColor: Color = ButtonDefaults.buttonColors().contentColor,
    ): CloseAndSaveHeaderColors {
        val materialSaveButtonColor = ButtonDefaults.buttonColors(
            containerColor = saveButtonContainerColor,
            contentColor = saveButtonContentColor
        )
        return CloseAndSaveHeaderColors(
            closeButtonColor = closeButtonColor,
            saveButtonColor = materialSaveButtonColor,
        )
    }
}

data class CloseAndSaveHeaderColors(
    val closeButtonColor: Color,
    val saveButtonColor: ButtonColors,
)