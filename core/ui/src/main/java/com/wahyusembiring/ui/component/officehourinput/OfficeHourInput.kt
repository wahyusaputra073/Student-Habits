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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.wahyusembiring.data.model.OfficeHour
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.component.dropdown.Dropdown
import com.wahyusembiring.ui.component.popup.picker.timepicker.TimePicker
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.UIText
import java.time.LocalTime

@Composable
fun OfficeHourInput(
    modifier: Modifier = Modifier,
    officeHours: List<OfficeHour>,
    onNewOfficeHour: (OfficeHour) -> Unit,
    onDeleteOfficeHour: (OfficeHour) -> Unit
) {
    var showOfficeHourDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = MaterialTheme.spacing.Medium),
                text = stringResource(R.string.office_hour),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { showOfficeHourDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        HorizontalDivider()
        Column(
            modifier = Modifier.padding(start = MaterialTheme.spacing.Large)
        ) {
            if (officeHours.isEmpty()) {
                EmptyOfficeHour()
            }
            for (officeHour in officeHours) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                OfficeHourListItem(officeHour = officeHour,
                    onDeleteClick = { onDeleteOfficeHour(officeHour)})
            }
        }
    }

    if (showOfficeHourDialog) {
        OfficeHourInputDialog(
            onDismissRequest = { showOfficeHourDialog = false },
            onOfficeHourAddClick = onNewOfficeHour
        )
    }
}

@Composable
fun OfficeHourListItem(
    officeHour: OfficeHour,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val days = stringArrayResource(id = R.array.days)

    Row(
        modifier = Modifier.requiredHeight(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_briefcase),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        VerticalDivider(
            modifier = Modifier.fillMaxHeight()
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
        Column {
            Text(
                text = days[officeHour.day]
            )
            Text(
                text = stringResource(
                    R.string.office_hour_from_to,
                    officeHour.startTime.hour,
                    officeHour.startTime.minute,
                    officeHour.endTime.hour,
                    officeHour.endTime.minute
                )
            )
        }

        Box {
            IconButton(
                onClick = { expanded = true },
                modifier = Modifier.size(24.dp) // Ukuran ikon titik tiga
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_more_vertical),
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.secondary // Menggunakan warna yang sama dengan ikon kontak
                )
            }
            // Dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = {
                        onDeleteClick()
                        expanded = false // Menutup dropdown setelah klik
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyOfficeHour() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.Medium,
                vertical = MaterialTheme.spacing.Small
            ),
            text = stringResource(R.string.no_office_hour),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontStyle = FontStyle.Italic
            )
        )
    }
}

enum class TimeType {
    START, END
}

@Composable
fun OfficeHourInputDialog(
    onDismissRequest: () -> Unit,
    initialOfficeHour: OfficeHour = OfficeHour(
        day = 1,
        startTime = LocalTime.of(7, 0),
        endTime = LocalTime.of(17, 0)
    ),
    onOfficeHourAddClick: (OfficeHour) -> Unit
) {
    var officeHour by remember {
        mutableStateOf(initialOfficeHour)
    }
    var showTimePickerDialog by remember {
        mutableStateOf<TimeType?>(null)
    }

    val days = stringArrayResource(id = R.array.days)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.enter_office_hour))
        },
        text = {
            Column {
                Text(text = stringResource(R.string.day))
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                Dropdown(
                    items = days.toList(),
                    selected = days[officeHour.day],
                    title = {
                        if (it != null) {
                            UIText.DynamicString(it)
                        } else {
                            UIText.StringResource(R.string.no_day_selected)
                        }
                    },
                    onItemClick = {
                        officeHour = officeHour.copy(
                            day = days.indexOf(it)
                        )
                    },
                    emptyContent = {}
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.from))
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                        Row(
                            modifier = Modifier
                                .clickable {
                                    showTimePickerDialog = TimeType.START
                                }
                                .border(
                                    color = MaterialTheme.colorScheme.primary,
                                    width = 1.dp,
                                    shape = MaterialTheme.shapes.small
                                ),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(vertical = MaterialTheme.spacing.Small)
                                    .padding(start = MaterialTheme.spacing.Small),
                                painter = painterResource(id = R.drawable.ic_clock),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        vertical = MaterialTheme.spacing.Small,
                                        horizontal = MaterialTheme.spacing.Medium
                                    ),
                                text = stringResource(
                                    R.string.time_colon,
                                    officeHour.startTime.hour.withZeroPadding(),
                                    officeHour.startTime.minute.withZeroPadding()
                                )
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = stringResource(R.string.until))
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                        Row(
                            modifier = Modifier
                                .clickable {
                                    showTimePickerDialog = TimeType.END
                                }
                                .border(
                                    color = MaterialTheme.colorScheme.primary,
                                    width = 1.dp,
                                    shape = MaterialTheme.shapes.small
                                ),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(vertical = MaterialTheme.spacing.Small)
                                    .padding(start = MaterialTheme.spacing.Small),
                                painter = painterResource(id = R.drawable.ic_clock),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                            Text(
                                modifier = Modifier
                                    .padding(
                                        vertical = MaterialTheme.spacing.Small,
                                        horizontal = MaterialTheme.spacing.Medium
                                    ),
                                text = stringResource(
                                    R.string.time_colon,
                                    officeHour.endTime.hour.withZeroPadding(),
                                    officeHour.endTime.minute.withZeroPadding()
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onOfficeHourAddClick(officeHour)
                    onDismissRequest().also { officeHour = initialOfficeHour }
                }
            ) {
                Text(text = stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
    if (showTimePickerDialog != null) {
        TimePicker(
            title = when (showTimePickerDialog!!) {
                TimeType.START -> stringResource(R.string.available_from)
                TimeType.END -> stringResource(R.string.until)
            },
            onDismissRequest = { showTimePickerDialog = null },
            onTimeSelected = {
                officeHour = when (showTimePickerDialog!!) {
                    TimeType.START -> officeHour.copy(startTime = it)
                    TimeType.END -> officeHour.copy(endTime = it)
                }
            }
        )
    }
}