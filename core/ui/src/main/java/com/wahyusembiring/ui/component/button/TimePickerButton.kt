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
import com.wahyusembiring.common.util.withZeroPadding
import com.wahyusembiring.ui.R
import java.time.LocalTime

@Composable
fun TimePickerButton(
    modifier: Modifier = Modifier,
    time: LocalTime?,
    label: String,
    onClicked: (() -> Unit)? = null,
) {
    ListItem(
        modifier = modifier
            .then(
                if (onClicked != null) {
                    Modifier.clickable {
                        onClicked()
                    }
                } else {
                    Modifier
                }
            ),
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_reminder),
                contentDescription = stringResource(R.string.add_reminder),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        headlineContent = {
            if (time != null) {
                Column {
                    Text(
                        text = "${time.hour.withZeroPadding()}:${time.minute.withZeroPadding()}"
                    )
                }
            } else {
                Text(
                    color = TextFieldDefaults.colors().disabledTextColor,
                    text = label,
                )
            }
        }
    )
}
