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
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.R

@Composable
fun AddReminderButton(
    modifier: Modifier = Modifier,
    time: Time?,
    onClicked: (() -> Unit)? = null,
    permissionCheck: (() -> Unit)? = null // Menambahkan parameter untuk memeriksa izin
) {
    ListItem(
        modifier = modifier
            .then(
                if (onClicked != null) {
                    Modifier.clickable {
                        // Jika ada fungsi permissionCheck, jalankan
                        permissionCheck?.invoke() ?: onClicked()
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
                        text = "${stringResource(R.string.remind_me_at)} ${time.hour.withZeroPadding()}:${time.minute.withZeroPadding()}"
                    )
                }
            } else {
                Text(
                    color = TextFieldDefaults.colors().disabledTextColor,
                    text = stringResource(id = R.string.add_reminder),
                )
            }
        }
    )
}
