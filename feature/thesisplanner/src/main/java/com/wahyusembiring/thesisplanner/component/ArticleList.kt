package com.wahyusembiring.thesisplanner.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.data.model.File
import com.wahyusembiring.thesisplanner.R

@Composable
internal fun ArticleList(
    articles: List<File>,
    onArticleClick: (File) -> Unit,
    onDeleteArticleClick: (File) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = articles,
            key = { it.uri }
        ) { article ->
            var moreOptionExpanded by remember { mutableStateOf(false) }

            ListItem(
                modifier = Modifier
                    .clickable {
                        onArticleClick(article)
                    },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pdf),
                        contentDescription = stringResource(R.string.pdf_icon)
                    )
                },
                headlineContent = {
                    Text(text = article.fileName)
                },
                trailingContent = {
                    Column {
                        IconButton(
                            onClick = {
                                moreOptionExpanded = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_more),
                                contentDescription = stringResource(R.string.more_option)
                            )
                        }
                        DropdownMenu(
                            expanded = moreOptionExpanded,
                            onDismissRequest = { moreOptionExpanded = false }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delete),
                                        contentDescription = stringResource(R.string.delete_article)
                                    )
                                },
                                text = {
                                    Text(text = stringResource(R.string.delete))
                                },
                                onClick = {
                                    onDeleteArticleClick(article)
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}