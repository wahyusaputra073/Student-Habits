package com.wahyusembiring.ui.component.popup.picker.datepicker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
   onDismissRequest: () -> Unit,
   onDateSelected: (Date) -> Unit
) {
   val datePickerState = rememberDatePickerState(
      initialSelectedDateMillis = System.currentTimeMillis(),
      initialDisplayedMonthMillis = System.currentTimeMillis()
   )
   val enableConfirmButton = remember {
      derivedStateOf { datePickerState.selectedDateMillis != null }
   }

   DatePickerDialog(
      modifier = Modifier.padding(MaterialTheme.spacing.Medium),
      onDismissRequest = onDismissRequest,
      confirmButton = {
         TextButton(
            onClick = {
               onDateSelected(Date(datePickerState.selectedDateMillis!!))
               onDismissRequest()
            },
            enabled = enableConfirmButton.value
         ) {
            Text(text = stringResource(R.string.confirm))
         }
      },
      dismissButton = {
         TextButton(
            onClick = onDismissRequest
         ) {
            Text(text = stringResource(R.string.cancel))
         }
      }
   ) {
      androidx.compose.material3.DatePicker(state = datePickerState)
   }
}