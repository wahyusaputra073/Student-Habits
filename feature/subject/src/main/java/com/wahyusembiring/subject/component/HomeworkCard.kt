package com.wahyusembiring.subject.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.datetime.Moment
import com.wahyusembiring.datetime.formatter.FormattingStyle
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.util.contrastColor
import java.util.Date

@Composable
fun HomeworkCard(
    subject: Subject,
    homework: Homework,
    date: Date,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier
            .clickable { onClick() },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = subject.color,
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (homework.score != null) {
                        homework.score.toString()
                    } else {
                        "?"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = subject.color.contrastColor()
                )
            }
        },
        headlineContent = {
            Column {
                Text(text = subject.name)
                Text(text = stringResource(id = R.string.homework))
            }
        },
        trailingContent = {
            Text(
                text = Moment.fromEpochMilliseconds(date.time).toString(FormattingStyle.INDO_SHORT)
            )
        }
    )
}