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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HomeworkCard(
    modifier: Modifier = Modifier,
    homeworkWithSubject: HomeworkWithSubject,
    onClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        border = BorderStroke(
            width = CardDefaults.outlinedCardBorder().width,
            color = homeworkWithSubject.subject.color
        ),
        onClick = onClick,
    ) {
        Header(
            color = homeworkWithSubject.subject.color,
            onDeleteClick = onDeleteClick
        )
        Body(
            title = homeworkWithSubject.homework.title,
            dueDate = homeworkWithSubject.homework.dueDate,
            deadline = homeworkWithSubject.homework.deadline,
            color = homeworkWithSubject.subject.color,
            isCompleted = homeworkWithSubject.homework.completed
        )
    }
}

@Composable
private fun Header(
    color: Color,
    onDeleteClick: () -> Unit,
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
                        color = color,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_homework),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
            Text(
                text = stringResource(R.string.homework),
                style = MaterialTheme.typography.titleMedium,
                color = color,
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
                    painter = painterResource(id = R.drawable.ic_more_vertical),
                    contentDescription = stringResource(R.string.more_options),
                    tint = color
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
}

@Composable
private fun Body(
    title: String,
    dueDate: LocalDateTime,
    deadline: LocalDateTime,
    color: Color,
    isCompleted: Boolean
) {
    val formattedDueDate = remember {
        dueDate
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT))
    }
    val formattedDeadline = remember {
        deadline
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
    }

    Column(
        modifier = Modifier.padding(
            horizontal = MaterialTheme.spacing.Medium,
        )
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            color = color,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
        Text(
            text = stringResource(R.string.assignment_date, formattedDueDate),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
        Text(
            text = stringResource(R.string.deadline, formattedDeadline),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )
        if (isCompleted) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_rounded_check),
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.Small))
                Text(
                    text = stringResource(R.string.completed),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
    }
}

@Preview
@Composable
private fun HomeworkCardPreview() {

    val homework = Homework(
        title = "Title",
        dueDate = LocalDateTime.now(),
        deadline = LocalDateTime.now(),
        completed = false,
        subjectId = "",
        dueReminder = null,
        deadlineReminder = null,
        notes = ""
    )

    val subject = Subject(
        name = "Subject",
        color = Color.Red,
        room = "",
        lecturerId = "",
        description = ""
    )

    HomeworkCard(
        homeworkWithSubject = HomeworkWithSubject(
            homework = homework,
            subject = subject
        )
    )
}