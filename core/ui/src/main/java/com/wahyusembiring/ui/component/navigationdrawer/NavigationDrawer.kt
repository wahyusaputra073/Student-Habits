package com.wahyusembiring.ui.component.navigationdrawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf


@Composable
fun NavigationDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    isGesturesEnabled: Boolean = true,
    selectedDrawerItem: DrawerItem,
    drawerItems: List<DrawerItem> = DrawerItem.defaultItems,
    onDrawerItemClick: (DrawerItem) -> Unit = {},
    imageResourceIdOrUri: Any? = R.drawable.app_icon,
    username: String? = null,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = isGesturesEnabled,
        drawerContent = {
            NavigationDrawerContent(
                drawerItems = drawerItems,
                selectedDrawerItem = selectedDrawerItem,
                onDrawerItemClick = onDrawerItemClick,
                imageResourceIdOrUri = imageResourceIdOrUri,
                username = username
            )
        },
        content = content
    )
}

@Composable
private fun NavigationDrawerContent(
    drawerItems: List<DrawerItem> = emptyList(),
    selectedDrawerItem: DrawerItem,
    onDrawerItemClick: (DrawerItem) -> Unit = {},
    imageResourceIdOrUri: Any?,
    username: String?
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .widthIn(min = 250.dp, max = 300.dp)
    ) {
        DrawerHeader(
            imageResourceIdOrUri = imageResourceIdOrUri,
            username = username
        )
        HorizontalDivider()
        DrawerBody(
            drawerItems = drawerItems,
            selectedDrawerItem = selectedDrawerItem,
            onDrawerItemClick = onDrawerItemClick
        )
    }
}

@Composable
private fun DrawerHeader(
    imageResourceIdOrUri: Any?,
    username: String?,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.spacing.Large)
            .padding(top = MaterialTheme.spacing.Large, bottom = MaterialTheme.spacing.Medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(100)),
            model = imageResourceIdOrUri ?: R.drawable.app_icon,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Habit",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (username.isNullOrBlank()) {
                    stringResource(R.string.guest_account)
                } else {
                    username
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ColumnScope.DrawerBody(
    drawerItems: List<DrawerItem> = emptyList(),
    selectedDrawerItem: DrawerItem,
    onDrawerItemClick: (DrawerItem) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
        drawerItems.forEachIndexed { index, drawerItem ->
            NavigationDrawerItem(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.Large),
                icon = drawerItem.icon?.let {
                    {
                        Icon(
                            painter = painterResource(id = drawerItem.icon),
                            contentDescription = stringResource(id = drawerItem.title)
                        )
                    }
                },
                label = { Text(text = stringResource(id = drawerItem.title)) },
                selected = selectedDrawerItem == drawerItem,
                onClick = {
                    onDrawerItemClick(drawerItem)
                }
            )
            if (drawerItem != drawerItems.last() && drawerItem.category != drawerItems[index + 1].category) {
                HorizontalDivider()
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
    }
}

