package com.wahyusembiring.ui.component.v2.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun <T> VerticalListContainer(
    modifier: Modifier = Modifier,
    items: List<T>,
    title: String,
    actionButton: @Composable () -> Unit = {},
    ifEmpty: @Composable () -> Unit = { EmptyItems() },
    content: @Composable (item: T) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = MaterialTheme.spacing.Medium),
                text = title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            actionButton()
        }
        HorizontalDivider()
        Column(
            modifier = Modifier.padding(start = MaterialTheme.spacing.Large)
        ) {
            if (items.isEmpty()) {
                ifEmpty()
            }
            for (item in items) {
                content(item)
            }
        }
    }
}

@Composable
fun EmptyItems(
    text: String = stringResource(R.string.no_items)
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.Medium,
                vertical = MaterialTheme.spacing.Small
            ),
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic
            )
        )
    }
}

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    leadingIcon: @Composable () -> Unit = {},
    trailingIcon: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .requiredHeight(IntrinsicSize.Min)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon()
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        VerticalDivider(
            modifier = Modifier.fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        content()
        trailingIcon()
    }
}