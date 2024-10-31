package com.wahyusembiring.ui.component.websiteinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing


@Composable
fun WebsiteInput(
    modifier: Modifier = Modifier,
    websites: List<String>,
    onNewWebsiteAddClick: (String) -> Unit,
) {
    var showWebsiteDialog by remember { mutableStateOf(false) }

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
                text = stringResource(R.string.website),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { showWebsiteDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider()
        Column(
            modifier = Modifier.padding(start = MaterialTheme.spacing.Large)
        ) {
            if (websites.isEmpty()) {
                EmptyWebsite()
            }
            for (website in websites) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                WebsiteListItem(website = website)
            }
        }
    }

    if (showWebsiteDialog) {
        WebsiteInputDialog(
            onDismissRequest = { showWebsiteDialog = false },
            onNewWebsiteAddClick = onNewWebsiteAddClick
        )
    }
}

@Composable
private fun WebsiteListItem(
    website: String
) {
    Row(
        modifier = Modifier.requiredHeight(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_link_internet),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        VerticalDivider(
            modifier = Modifier.fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        Text(
            text = website
        )
    }
}

@Composable
private fun EmptyWebsite() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.Medium,
                vertical = MaterialTheme.spacing.Small
            ),
            text = stringResource(R.string.no_website),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic
            )
        )
    }
}

@Composable
private fun WebsiteInputDialog(
    onDismissRequest: () -> Unit,
    onNewWebsiteAddClick: (String) -> Unit
) {
    var website by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.enter_website_link))
        },
        text = {
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text(text = stringResource(R.string.website)) },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onNewWebsiteAddClick(website)
                    onDismissRequest().also { website = "" }
                }
            ) {
                Text(text = stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}