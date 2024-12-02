package com.wahyusembiring.ui.component.v2.datetimepicker

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

class DateTimePickerState(
    private val initialDateTime: LocalDateTime = LocalDateTime.now(),
) {
    private val dateTimeState = mutableStateOf(initialDateTime)
    var selectedDateTime: LocalDateTime
        get() = dateTimeState.value
        set(value) {
            dateTimeState.value = value
        }
}

@Composable
fun rememberDateTimePickerState(
    initialDateTime: LocalDateTime = LocalDateTime.now(),
): DateTimePickerState {
    return remember { DateTimePickerState(initialDateTime) }
}

@Composable
fun DateTimePickerButton(
    dateTime: LocalDateTime = LocalDateTime.now(),
    enabled: Boolean = true,
    errorMessage: String? = null,
    onClick: () -> Unit = {}
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(dateTime) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent =
                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        onClick()
                    }
                }
            },
        value = dateTime.atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter
                    .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                    .withLocale(Locale.getDefault())
            ),
        onValueChange = {},
        readOnly = true,
        enabled = enabled,
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.EditCalendar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Rounded.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            {
                Text(errorMessage)
            }
        } else null
    )
}

@Composable
fun SeparateDateTimePickerButton(
    dateTime: LocalDateTime = LocalDateTime.now(),
    enableDateButton: Boolean = true,
    enableTimeButton: Boolean = true,
    errorMessage: String? = null,
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {}
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = dateTime.atZone(ZoneId.systemDefault())
            .format(
                DateTimeFormatter
                    .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                    .withLocale(Locale.getDefault())
            ),
        onValueChange = {},
        readOnly = true,
        leadingIcon = {
            IconButton(
                enabled = enableDateButton,
                onClick = onDateClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.EditCalendar,
                    contentDescription = null,
                    tint = if (errorMessage == null) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        },
        trailingIcon = {
            IconButton(
                enabled = enableTimeButton,
                onClick = onTimeClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccessTime,
                    contentDescription = null,
                    tint = if (errorMessage == null) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        },
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            {
                Text(errorMessage)
            }
        } else null
    )
}

@Composable
fun DateTimePicker(
    state: DateTimePickerState = rememberDateTimePickerState(),
    title: String = stringResource(R.string.select_time),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDismissRequest: () -> Unit,
    datePickerStyle: DatePickerStyle = DatePickerStyle.SPINNER,
    timePickerStyle: TimePickerStyle = TimePickerStyle.CLOCK,
    allowedDateTime: ((LocalDateTime) -> Boolean)? = null,
    onDateTimeSelected: (LocalDateTime) -> Unit,
) {

    Dialog(
        onDismissRequest = onDismissRequest
    ) {

        var isNight by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current
        val timePickerState = rememberTimePickerState(
            initialTime = state.selectedDateTime.toLocalTime()
        )

        LaunchedEffect(state.selectedDateTime, allowedDateTime) {
            val isInAllowedTime = allowedDateTime?.invoke(state.selectedDateTime) ?: true
            errorMessage = if (isInAllowedTime) {
                null
            } else {
                context.getString(R.string.the_selected_time_is_not_allowed)
            }
        }

        LaunchedEffect(timePickerState.selectedTime) {
            state.selectedDateTime = state.selectedDateTime.with(timePickerState.selectedTime)
        }

        Surface(
            shape = MaterialTheme.shapes.medium
        ) {
            Box(
                modifier = Modifier.height(IntrinsicSize.Max)
            ) {
                AnimatedDayPhasedBackground(
                    time = state.selectedDateTime.toLocalTime(),
                    onDayChange = { isNight = it }
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePickerHeader(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.Large)
                            .padding(top = MaterialTheme.spacing.Large, bottom = MaterialTheme.spacing.Medium),
                        state = timePickerState,
                        title = title,
                        isNight = isNight,
                        onConfirmButtonClick = {
                            onDateTimeSelected(state.selectedDateTime)
                        },
                        isButtonEnabled = errorMessage == null,
                        errorMessage = errorMessage,
                    )
                    DatePickerButton(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Large)
                            .padding(bottom = MaterialTheme.spacing.Medium),
                        date = state.selectedDateTime.toLocalDate(),
                        onClick = {
                            when (datePickerStyle) {
                                DatePickerStyle.SPINNER -> {
                                    showDatePickerSpinner(
                                        context = context,
                                        isDarkMode = isNight,
                                        initialDate = state.selectedDateTime.toLocalDate(),
                                        minDate = minDate,
                                        maxDate = maxDate,
                                        onDateSelected = {
                                            state.selectedDateTime = state.selectedDateTime.with(it)
                                        }
                                    )
                                }
                                DatePickerStyle.CALENDAR -> Unit
                            }
                        }
                    )
                    when (timePickerStyle) {
                        TimePickerStyle.SPINNER -> Unit
                        TimePickerStyle.CLOCK -> {
                            MaterialTimePicker(state = timePickerState)
                        }
                    }
                    Spacer(modifier = Modifier.height(130.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimePickerPreview() {
    HabitTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            DateTimePicker(
                onDismissRequest = {},
                onDateTimeSelected = {},
            )
        }
    }
}