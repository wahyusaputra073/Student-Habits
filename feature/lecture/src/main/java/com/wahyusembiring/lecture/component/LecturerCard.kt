package com.wahyusembiring.lecture.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.lecture.R
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.theme.spacing

@Composable
fun LecturerCard(
    lecturerWithSubjects: LecturerWithSubject,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit, // Tambahkan parameter onDeleteClick untuk aksi delete
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } // State untuk menampilkan DropdownMenu

    ListItem(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                if (lecturerWithSubjects.lecturer.photo == null) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.spacing.ExtraSmall),
                        painter = painterResource(id = R.drawable.ic_person),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null
                    )
                } else {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = lecturerWithSubjects.lecturer.photo,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }
        },
        headlineContent = {
            Text(text = lecturerWithSubjects.lecturer.name)
        },
        supportingContent = {
            Text(
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = lecturerWithSubjects.subjects.joinToString { it.name }.ifBlank {
                    stringResource(R.string.no_subject_added)
                }
            )
        },
        trailingContent = {
            IconButton(
                onClick = {
                    expanded = true
                }
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
                        onDeleteClick() // Memanggil aksi delete
                    },
                    text = {
                        Text(text = stringResource(R.string.delete_lecture)) // Text "Delete"
                    }
                )
            }
        }
    )
}


