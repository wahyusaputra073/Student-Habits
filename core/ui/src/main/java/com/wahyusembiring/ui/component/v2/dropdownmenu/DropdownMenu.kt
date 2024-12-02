package com.wahyusembiring.ui.component.v2.dropdownmenu

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.ui.R

@Composable
fun ActionDropdownIconButton(
    expanded: Boolean,
    onClick: () -> Unit,
    onDropdownDismissRequest: () -> Unit,
    dropdownContent: @Composable ColumnScope.() -> Unit,
) {
    Column {
        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.more_options),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDropdownDismissRequest,
            content = dropdownContent
        )
    }
}