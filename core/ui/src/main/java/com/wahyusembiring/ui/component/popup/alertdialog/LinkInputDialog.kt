package com.wahyusembiring.ui.component.popup.alertdialog

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkInputDialog(
   onDismissRequest: () -> Unit,
   onLinkConfirmed: (uri: Uri) -> Unit
) {
   var link by remember { mutableStateOf("") }

   BasicAlertDialog(
      onDismissRequest = onDismissRequest
   ) {
      Surface(
         shape = AlertDialogDefaults.shape,
         tonalElevation = AlertDialogDefaults.TonalElevation,
         color = AlertDialogDefaults.containerColor,
      ) {
         Column(
            modifier = Modifier
               .padding(MaterialTheme.spacing.Large)
         ) {
            OutlinedTextField(
               modifier = Modifier
                  .fillMaxWidth(),
               value = link,
               onValueChange = { link = it },
               label = { Text(text = "URL Link:") },
               singleLine = true,
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            Row(
               modifier = Modifier
                  .fillMaxWidth(),
               horizontalArrangement = Arrangement.End
            ) {
               TextButton(onClick = onDismissRequest) {
                  Text(text = stringResource(R.string.cancel))
               }
               TextButton(
                  onClick = {
                     onLinkConfirmed(Uri.parse(link))
                     onDismissRequest()
                  }
               ) {
                  Text(text = stringResource(R.string.confirm))
               }
            }
         }
      }
   }
}

@Preview(showBackground = true)
@Composable
private fun LinkInputDialogPreview() {
   HabitTheme {
      Box(
         modifier = Modifier.fillMaxSize(),
         contentAlignment = Alignment.Center
      ) {
         LinkInputDialog(
            onDismissRequest = {},
            onLinkConfirmed = {}
         )
      }
   }
}