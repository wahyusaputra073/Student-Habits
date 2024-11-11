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
//import androidx.navigation.NavController
//import androidx.navigation.NavOptionsBuilder
//import androidx. navigation. PopUpToBuilder
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
//import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.datetime.Moment
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlineTimePicker(
    modifier: Modifier = Modifier,
//    navController: NavHostController,
    title: String = stringResource(R.string.when_deadline),
    onTimeSelected: (time: DeadlineTime) -> Unit,
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
                            val time = DeadlineTime(timePickerState.hour, timePickerState.minute)
                            onTimeSelected(time)
                            onDismissRequest()
//                            navController.navigate(Screen.CreateExam) {
//                                popUpTo(Screen.CreateExam) { inclusive = true }
//                            }
                        }
                    )
                    {
                        Text(text = stringResource(id = R.string.confirm))
                    }
                }
            }
        }
    }
}