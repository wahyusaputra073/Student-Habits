package com.wahyusembiring.ui.component.popup.picker.timepicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.datetime.Moment
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.when_do_you_want_to_be_reminded),
    onTimeSelected: (time: LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val moment = remember { Moment.now() }
    val timePickerState = rememberTimePickerState(
        initialHour = moment.hour,
        initialMinute = moment.minute
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = modifier,
            color = AlertDialogDefaults.containerColor,
            shape = AlertDialogDefaults.shape,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.Large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.spacing.Large)
                        .padding(bottom = MaterialTheme.spacing.Medium),
                    color = MaterialTheme.colorScheme.primary,
                    text = title
                )
                androidx.compose.material3.TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.Large),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                            onTimeSelected(time)
                            onDismissRequest()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            TimePicker(
                onTimeSelected = {},
                onDismissRequest = {},
            )
        }
    }
}