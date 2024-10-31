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
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.R

@Composable
fun AddSubjectButton(
    modifier: Modifier = Modifier,
    subject: Subject?,
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
                painter = painterResource(id = R.drawable.ic_subjects),
                contentDescription = stringResource(R.string.add_subject),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        headlineContent = {
            if (subject != null) {
                Column {
                    Text(text = subject.name)
                }
            } else {
                Text(
                    color = TextFieldDefaults.colors().disabledTextColor,
                    text = stringResource(R.string.add_subject),
                )
            }
        }
    )
}