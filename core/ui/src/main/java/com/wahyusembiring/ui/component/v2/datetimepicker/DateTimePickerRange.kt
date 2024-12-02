package com.wahyusembiring.ui.component.v2.datetimepicker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone


class DateTimePickerRangeState(
    private val initialStartDate: LocalDateTime = LocalDateTime.now(),
    private val initialEndDate: LocalDateTime = initialStartDate.plusWeeks(1L).with(LocalTime.MAX)
) {

    private val startDateState = mutableStateOf(initialStartDate)
    private val endDateState = mutableStateOf(initialEndDate)

    var startDate: LocalDateTime
        get() = startDateState.value
        set(value) {
            startDateState.value = value
        }

    var endDate: LocalDateTime
        get() = endDateState.value
        set(value) {
            endDateState.value = value
        }

}

@Composable
fun rememberDateTimePickerRangeState(
    initialStartDate: LocalDateTime = LocalDateTime.now(),
    initialEndDate: LocalDateTime = initialStartDate.plusWeeks(1L).with(LocalTime.MAX)
) = remember {
    DateTimePickerRangeState(initialStartDate, initialEndDate)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerRange(
    state: DateTimePickerRangeState = rememberDateTimePickerRangeState(),
    onCanceled: () -> Unit,
    onDateSelected: (ClosedRange<LocalDateTime>) -> Unit,
) {

    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val focusRequester = remember { FocusRequester() }
    var showDueTimePicker by remember { mutableStateOf(false) }
    var showDeadlineTimePicker by remember { mutableStateOf(false) }
    var deadlineErrorMessage by remember { mutableStateOf<String?>(null) }

    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = state.startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        initialSelectedEndDateMillis = state.endDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        selectableDates = OnlyFutureDates
    )
    val dueTimePickerState = rememberTimePickerState(
        initialTime = state.startDate.toLocalTime()
    )
    val deadlineTimePickerState = rememberTimePickerState(
        initialTime = state.endDate.toLocalTime()
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        state.apply {
            startDate = dateRangePickerState.selectedStartDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime().with(startDate.toLocalTime())
            } ?: LocalDateTime.now()
            endDate = dateRangePickerState.selectedEndDateMillis?.let {
                Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDateTime().with(endDate.toLocalTime())
            } ?: startDate.with(endDate.toLocalTime())
        }
    }

    LaunchedEffect(state.startDate, state.endDate) {
        if (state.startDate > state.endDate) {
            deadlineErrorMessage = context.getString(R.string.deadline_cannot_be_before_start_date)
        }
    }

    Popup(
        onDismissRequest = onCanceled,
        properties = PopupProperties(dismissOnClickOutside = false)
    ) {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = MaterialTheme.spacing.Large,
                            vertical = MaterialTheme.spacing.Medium
                        )
                ) {
                    Text(
                        text = stringResource(R.string.start_date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    SeparateDateTimePickerButton(
                        dateTime = state.startDate,
                        onDateClick = {
                            showDatePickerSpinner(
                                context = context,
                                isDarkMode = isDarkMode,
                                initialDate = state.startDate.toLocalDate(),
                                minDate = LocalDate.now()
                            ) {
                                dateRangePickerState.setSelection(
                                    startDateMillis = it.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                    endDateMillis = state.endDate.with(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli()
                                )
                            }
                        },
                        onTimeClick = {
                            showDueTimePicker = true
                        },
                    )
                }
                DateRangePicker(
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    title = null,
                    headline = null,
                    showModeToggle = false,
                    state = dateRangePickerState,
                )
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = MaterialTheme.spacing.Large,
                            vertical = MaterialTheme.spacing.Medium
                        )
                ) {
                    Text(
                        text = stringResource(R.string.deadline_double_colon),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    SeparateDateTimePickerButton(
                        dateTime = state.endDate,
                        onDateClick = {
                            showDatePickerSpinner(
                                context = context,
                                isDarkMode = isDarkMode,
                                initialDate = state.endDate.toLocalDate(),
                                minDate = state.startDate.toLocalDate()
                            ) {
                                dateRangePickerState.setSelection(
                                    startDateMillis = state.startDate.with(LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli(),
                                    endDateMillis = it.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                                )
                            }
                        },
                        onTimeClick = {
                            showDeadlineTimePicker = true
                        },
                        errorMessage = deadlineErrorMessage
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = MaterialTheme.spacing.Medium,
                            bottom = MaterialTheme.spacing.Large
                        )
                        .padding(horizontal = MaterialTheme.spacing.Large),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onCanceled
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.padding(MaterialTheme.spacing.Small))
                    Button(
                        enabled = deadlineErrorMessage == null,
                        onClick = {
                            onDateSelected(state.startDate.rangeTo(state.endDate))
                        }
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }

        if (showDueTimePicker) {
            TimePicker(
                state = dueTimePickerState,
                onDismissRequest = {
                    showDueTimePicker = false
                },
                onTimeSelected = {
                    state.apply {
                        startDate = startDate.with(it)
                        showDueTimePicker = false
                    }
                }
            )
        }
        if (showDeadlineTimePicker) {
            TimePicker(
                state = deadlineTimePickerState,
                onDismissRequest = {
                    showDeadlineTimePicker = false
                },
                onTimeSelected = {
                    state.apply {
                        endDate = endDate.with(it)
                        showDeadlineTimePicker = false
                    }
                }
            )
        }

    }
}

@Preview
@Composable
private fun DateTimePickerRangePreview() {
    HabitTheme {
        Surface (
            modifier = Modifier
                .fillMaxSize()
        ) {
            DateTimePickerRange(
                onCanceled = {},
                onDateSelected = {}
            )
        }
    }
}