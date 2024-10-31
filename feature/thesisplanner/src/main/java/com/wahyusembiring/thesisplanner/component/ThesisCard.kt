package com.wahyusembiring.thesisplanner.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wahyusembiring.thesisplanner.R
import com.wahyusembiring.thesisplanner.screen.thesisselection.Thesis
import com.wahyusembiring.ui.theme.spacing

@Composable
internal fun ThesisCard(
    modifier: Modifier = Modifier,
    thesis: Thesis,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Header(onDeleteClick)
        Body(
            title = thesis.thesis.title,
            finishedTasks = Pair(
                thesis.tasks.filter { it.isCompleted }.size,
                thesis.tasks.size
            ),
            articles = thesis.thesis.articles.size
        )
//        Footer()
    }
}

@Composable
private fun Header(
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.Medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_pen),
                    contentDescription = stringResource(R.string.thesis_icon),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
            Text(
                text = stringResource(R.string.thesis),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            var optionExpanded by remember { mutableStateOf(false) }

            IconButton(
                onClick = { optionExpanded = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = stringResource(R.string.more_options)
                )
            }
            DropdownMenu(
                expanded = optionExpanded,
                onDismissRequest = { optionExpanded = false }
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.delete_thesis),
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    text = {
                        Text(text = stringResource(id = R.string.delete))
                    },
                    onClick = {
                        optionExpanded = false
                        onDeleteClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun Body(
    title: String,
    finishedTasks: Pair<Int, Int>,
    articles: Int
) {
    Column(
        modifier = Modifier.padding(
            horizontal = MaterialTheme.spacing.Medium,
        )
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
        Text(
            text = stringResource(
                R.string.tasks_finished,
                finishedTasks.first,
                finishedTasks.second
            ),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
        Text(
            text = stringResource(R.string.articles_count, articles),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
    }
}

@Composable
private fun Footer() {

}