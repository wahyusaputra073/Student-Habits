package com.wahyusembiring.ui.component.eventcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.imageLoader
import com.wahyusembiring.common.util.withZeroPadding
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.UIText


data class EventCard(
    val title: UIText,
    val date: UIText,
    val events: List<Any>
)

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    events: List<Any> = emptyList(),
    onEventCheckedChange: (event: Any, isChecked: Boolean) -> Unit = { _, _ -> },
//    onShowMoreClick: () -> Unit = {},
//    onAddEventClick: () -> Unit = {},
    onEventClick: (event: Any) -> Unit = {},
    onDeletedEventClick: (event: Any) -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Header()
        if (events.isEmpty()) {
            NoEventBody()
        } else {
            Body(
                events = events,
                onEventCheckedChange = onEventCheckedChange,
                onClick = onEventClick,
                onDeleteEventClick = onDeletedEventClick
            )
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
//        Footer(
//            onShowMoreClick = onShowMoreClick,
//            onAddEventClick = onAddEventClick
//        )
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.Medium),
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
                painter = painterResource(id = R.drawable.ic_event),
                contentDescription = stringResource(R.string.event_icon),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
        Text(
            text = stringResource(R.string.event_overview),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Body(
    events: List<Any>,
    onClick: (event: Any) -> Unit = {},
    onDeleteEventClick: (event: Any) -> Unit = {},
    onEventCheckedChange: (event: Any, isChecked: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.Medium),
    ) {
        for (event in events) {
            when (event) {
                is ExamWithSubject -> {
                    BodyEventList(
                        onClick = { onClick(event) },
                        onDeletedClick = { onDeleteEventClick(event) },
                        isChecked = event.exam.score != null,
                        onCheckedChange = { onEventCheckedChange(event, it) },
                        title = event.exam.title,
                        subjectColor = event.subject.color,
                        subjectName = event.subject.name,
                        eventType = stringResource(R.string.exam),
                        times = event.exam.deadline
                    )
                }

                is HomeworkWithSubject -> {
                    BodyEventList(
                        onClick = { onClick(event) },
                        onDeletedClick = { onDeleteEventClick(event) },
                        isChecked = event.homework.completed,
                        onCheckedChange = { onEventCheckedChange(event, it) },
                        title = event.homework.title,
                        subjectColor = event.subject.color,
                        subjectName = event.subject.name,
                        eventType = stringResource(R.string.task),
                        times = event.homework.deadline
                    )
                }

                is Reminder -> {
                    BodyEventList(
                        onClick = { onClick(event) },
                        onDeletedClick = { onDeleteEventClick(event) },
                        isChecked = event.completed,
                        onCheckedChange = { onEventCheckedChange(event, it) },
                        title = event.title,
                        subjectColor = null,
                        subjectName = null,
                        eventType = stringResource(R.string.reminder),
                        times = null

                    )
                }
            }
        }
    }
}

@Composable
private fun BodyEventList(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    times: DeadlineTime?,
    onClick: () -> Unit = {},
    onDeletedClick: () -> Unit = {},
    subjectColor: Color?,
    subjectName: String?,
    eventType: String
) {
    var checkBoxWidth by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ){


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Checkbox(
                modifier = Modifier.onSizeChanged { checkBoxWidth = it.width },
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )

            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))

            Column(){
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.let {
                        if (!isChecked) it else it.copy(
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                )

                Row(){
                    Text(
                        text = "($eventType)",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))

                    if (times != null) {
                        Text(
                            text = "${times.hour.withZeroPadding()}:${times.minute.withZeroPadding()}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }



                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (subjectName != null && subjectColor != null) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = subjectColor,
                                    shape = RoundedCornerShape(50)
                                )
                        )

                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))


                        Text(
                            text = subjectName,
                            style = MaterialTheme.typography.bodyMedium
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column {
                var expanded by remember { mutableStateOf(false) }

                IconButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more_vertical),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    DropdownMenuItem(
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_delete),
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(R.string.delete),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = { onDeletedClick() }
                    )
                }
            }

        }
//        Column() {}
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))

    }
}

@Composable
private fun NoEventBody() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.Medium),
    ) {
        AsyncImage(
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.Medium),
            model = R.drawable.relaxing,
            contentDescription = stringResource(R.string.no_event_picture),
            imageLoader = LocalContext.current.imageLoader
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
        Text(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.Small),
            text = "There are no events",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "You can add new events with the button in the bottom right corner",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Footer(
    onShowMoreClick: () -> Unit = {},
    onAddEventClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.Medium),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(
            onClick = onShowMoreClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_read_more),
                contentDescription = stringResource(R.string.show_more)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
            Text(text = stringResource(R.string.show_more))
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
        Button(
            onClick = onAddEventClick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = stringResource(R.string.add_new_event)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
            Text(text = stringResource(R.string.new_event))
        }
    }
}

@Preview
@Composable
private fun EventCardPreview() {
    EventCard()
}