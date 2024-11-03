package com.wahyusembiring.ui.component.modalbottomsheet.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.R

@Composable
fun SubjectListItemMenu(
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
    subject: Subject,
    onClicked: ((subject: Subject) -> Unit)? = null,
    onDeleteSubClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        colors = colors,
        modifier = modifier
            .then(
                if (onClicked != null) {
                    Modifier.clickable { onClicked(subject) }
                } else {
                    Modifier
                }
            ),
        leadingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_subjects),
                contentDescription = subject.name,
                tint = subject.color
            )
        },
        headlineContent = {
            Text(text = subject.name)
        },
        trailingContent = {
            IconButton(
                onClick = {expanded = true}
            ) {
                Icon(
                    painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_more_vertical),
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onDeleteSubClick() // Memanggil aksi delete
                    },
                    text = {
                        Text(text = stringResource(R.string.delete)) // Text "Delete"
                    }
                )
            }
        }
    )
}