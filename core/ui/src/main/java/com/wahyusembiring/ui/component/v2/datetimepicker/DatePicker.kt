package com.wahyusembiring.ui.component.v2.datetimepicker

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.ui.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
internal object OnlyFutureDates: SelectableDates {
    private val currentInstant = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return !Instant.ofEpochMilli(utcTimeMillis).isBefore(currentInstant)
    }
}

@Suppress("ObjectLiteralToLambda")
internal fun showDatePickerSpinner(
    context: Context,
    isDarkMode: Boolean,
    initialDate: LocalDate = LocalDate.now(),
    minDate: LocalDate? = null,
    maxDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit
) {
    val listener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(datePicker: DatePicker?, year: Int, mounth: Int, day: Int) {
            onDateSelected(LocalDate.of(year, mounth + 1, day))
        }
    }
    val theme = if (isDarkMode) AlertDialog.THEME_HOLO_DARK else AlertDialog.THEME_HOLO_LIGHT
    val datePickerDialog = DatePickerDialog(context, theme, listener, initialDate.year, initialDate.monthValue - 1, initialDate.dayOfMonth)
    datePickerDialog.datePicker.calendarViewShown = false
    minDate?.let { datePickerDialog.datePicker.minDate = it.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli() }
    maxDate?.let { datePickerDialog.datePicker.maxDate = it.atStartOfDay(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli() }
    datePickerDialog.show()
}

enum class DatePickerStyle {
    SPINNER, CALENDAR
}

@Composable
fun DatePickerButton(
    modifier: Modifier = Modifier,
    date: LocalDate? = null,
    enabled: Boolean = true,
    errorMessage: String? = null,
    onClick: () -> Unit = {}
) {
    OutlinedTextField(
        modifier = modifier
            .pointerInput(date) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent =
                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        onClick()
                    }
                }
            },
        value = date?.atStartOfDay(ZoneId.systemDefault())?.format(
            DateTimeFormatter
                .ofLocalizedDate(FormatStyle.LONG)
                .withLocale(Locale.getDefault())
        ) ?: "",
        placeholder = {
            Text(
                text = stringResource(R.string.please_select_a_date),
                style = MaterialTheme.typography.bodyLarge,
                color = OutlinedTextFieldDefaults.colors().disabledTextColor
            )
        },
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
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            {
                Text(errorMessage)
            }
        } else null
    )
}