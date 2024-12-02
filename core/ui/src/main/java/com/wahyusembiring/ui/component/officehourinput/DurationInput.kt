package com.wahyusembiring.ui.component.officehourinput

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.wahyusembiring.common.util.withZeroPadding
import com.wahyusembiring.data.model.SpanTime
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.component.popup.picker.timepicker.TimePicker
import com.wahyusembiring.ui.theme.spacing



@Composable
fun DurationInput(
    modifier: Modifier = Modifier,
    durationTime: SpanTime?,
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
            if (durationTime != null) {
                Column {
                    Text(
                        text = stringResource(
                            R.string.event_from_to,
                            durationTime.startTime.hour.withZeroPadding(),
                            durationTime.startTime.minute.withZeroPadding(),
                            durationTime.endTime.hour.withZeroPadding(),
                            durationTime.endTime.minute.withZeroPadding(),
                        )
                    )
                }
            } else {
                Text(
                    color = TextFieldDefaults.colors().disabledTextColor,
                    text = stringResource(id = R.string.add_end_time),
                )
            }
        }
    )
}

