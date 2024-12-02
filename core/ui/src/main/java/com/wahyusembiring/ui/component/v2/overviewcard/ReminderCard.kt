package com.wahyusembiring.ui.component.v2.overviewcard

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ReminderCard(
    modifier: Modifier = Modifier,
    reminder: Reminder,
    dateTime: LocalDateTime,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        onClick = onClick,
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            leadingContent = {
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
                        painter = painterResource(id = R.drawable.ic_reminder),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            overlineContent = {
                Text(
                    text = stringResource(R.string.reminder),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            },
            headlineContent = {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            },
            trailingContent = {
                Column(
                    verticalArrangement = Arrangement.Center
                ) {
                    var optionExpanded by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = { optionExpanded = true }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more_vertical),
                            contentDescription = stringResource(R.string.more_options),
                            tint = MaterialTheme.colorScheme.primary
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
                                    contentDescription = null,
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
        )
        Body(dateTime)
    }
}

@Composable
private fun Body(
    dateTime: LocalDateTime
) {
    val formattedDate = remember {
        dateTime
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
    }

    Column(
        modifier = Modifier.padding(
            horizontal = MaterialTheme.spacing.Medium,
        )
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = formattedDate,
            onValueChange = {},
            readOnly = true,
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
    }
}

@Preview
@Composable
private fun ReminderCardPreview() {
    ReminderCard(
        reminder = Reminder(
            title = "Reminder 1",
            reminderDates = emptyList(),
            notes = ""
        ),
        dateTime = LocalDateTime.now()
    )
}