package com.wahyusembiring.ui.component.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.datetime.Moment
import com.wahyusembiring.datetime.formatter.FormattingStyle
import com.wahyusembiring.ui.R
import java.time.LocalDate
import kotlin.time.Duration.Companion.days


@Composable
fun DatePickerButton(
    modifier: Modifier = Modifier,
    date: LocalDate?,
    onClicked: (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier
            .then(
                if (onClicked != null) {
                    Modifier.clickable { onClicked() }
                } else {
                    Modifier
                }
            ),
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_date_picker),
                contentDescription = stringResource(R.string.pick_date),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        headlineContent = {
            if (date != null) {
                Column {
                    Text(
                        text = Moment.fromEpochMilliseconds(date.toEpochDay().days.inWholeMilliseconds)
                            .toString(FormattingStyle.INDO_FULL)
                    )
                }
            } else {
                Text(
                    color = TextFieldDefaults.colors().disabledTextColor,
                    text = stringResource(R.string.add_date),
                )
            }
        }
    )
}