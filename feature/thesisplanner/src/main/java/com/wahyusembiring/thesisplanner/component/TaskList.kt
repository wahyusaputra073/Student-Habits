package com.wahyusembiring.thesisplanner.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.text.style.TextDecoration
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.datetime.Moment
import com.wahyusembiring.datetime.formatter.FormattingStyle
import com.wahyusembiring.thesisplanner.R
import kotlin.time.Duration.Companion.days


@Composable
internal fun TaskList(
    tasks: List<Task>,
    onCompletedStatusChange: (Task, Boolean) -> Unit,
    onDeleteTaskClick: (Task) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = tasks,
            key = { it.id }
        ) { task ->
            var moreOptionExpanded by remember { mutableStateOf(false) }

            ListItem(
                overlineContent = {
                    Text(
                        text = Moment
                            .fromEpochMilliseconds(task.dueDate.toEpochDay().days.inWholeMilliseconds)
                            .toString(FormattingStyle.INDO_SHORT),
                    )
                },
                headlineContent = {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.bodyMedium
                            .copy(
                                textDecoration = if (task.isCompleted) {
                                    TextDecoration.LineThrough
                                } else {
                                    TextDecoration.None
                                }
                            )
                    )
                },
                leadingContent = {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = {
                            onCompletedStatusChange(task, it)
                        }
                    )
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
                                        contentDescription = stringResource(R.string.delete_task)
                                    )
                                },
                                text = {
                                    Text(text = stringResource(R.string.delete))
                                },
                                onClick = {
                                    onDeleteTaskClick(task)
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}