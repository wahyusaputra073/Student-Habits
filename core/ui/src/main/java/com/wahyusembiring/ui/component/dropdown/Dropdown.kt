package com.wahyusembiring.ui.component.dropdown

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toIntRect
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.UIText

@Composable
fun <T : Any> Dropdown(
    modifier: Modifier = Modifier,
    items: List<T>,
    selected: T?,
    icons: ((item: T) -> Painter)? = null,
    title: (item: T?) -> UIText,
    onItemClick: (item: T) -> Unit,
    emptyContent: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        horizontal = MaterialTheme.spacing.Medium,
                        vertical = MaterialTheme.spacing.Small
                    ),
                text = title(selected).asString()
            )
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown),
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.Medium),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (items.isEmpty()) emptyContent()
            for (item in items) {
                DropdownMenuItem(
                    leadingIcon = icons?.let {
                        { Icon(painter = it(item), contentDescription = null) }
                    },
                    text = {
                        Text(
                            text = title(item).asString(),
                            color = if (item == selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onItemClick(item)
                        expanded = false
                    }
                )
            }
        }
    }
}