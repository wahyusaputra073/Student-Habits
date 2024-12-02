package com.wahyusembiring.ui.component.v2.lecturerpicker

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.component.v2.list.ListItem
import com.wahyusembiring.ui.theme.spacing

@Composable
fun LecturerPickerButton(
    modifier: Modifier = Modifier,
    lecturer: Lecturer? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedTextField(
        modifier = modifier
            .pointerInput(lecturer) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent =
                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        onClick()
                    }
                }
            },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_teachers),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        value = lecturer?.name ?: "",
        onValueChange = {},
        enabled = enabled,
        singleLine = true,
        maxLines = 1,
        readOnly = true,
        placeholder = {
            Text(
                text = stringResource(R.string.please_choose_a_lecturer_for_this_subject),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerPicker(
    lecturers: List<Lecturer>,
    onDismissRequest: () -> Unit,
    onAddNewLecturerButtonClick: () -> Unit,
    onLecturerSelected: (Lecturer) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.spacing.Small)
                    .padding(bottom = MaterialTheme.spacing.Small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.Medium),
                    text = stringResource(R.string.select_lecturer),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onAddNewLecturerButtonClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )
                }
            }
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 150.dp)
            ) {
                if (lecturers.isEmpty()) {
                    EmptyLecturer()
                }
                for (lecturer in lecturers) {
                    ListItem(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.Large),
                        onClick = {
                            onLecturerSelected(lecturer)
                            onDismissRequest()
                        },
                        content = {
                            Text(
                                text = lecturer.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(vertical = MaterialTheme.spacing.Small),
                                painter = painterResource(R.drawable.ic_teachers),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLecturer() {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No lecturers found",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}