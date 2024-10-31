package com.wahyusembiring.ui.component.popup.alertdialog.information

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wahyusembiring.ui.theme.spacing

@Composable
fun InformationAlertDialog(
   onButtonClicked: () -> Unit,
   buttonText: String,
   title: String,
   message: String,
   onDismissRequest: () -> Unit,
) {
   AlertDialog(
      onDismissRequest = onDismissRequest,
      confirmButton = {
         TextButton(onClick = onButtonClicked) {
            Text(text = buttonText)
         }
      },
      title = {
         Text(
            modifier = Modifier
               .padding(bottom = MaterialTheme.spacing.Medium),
            style = MaterialTheme.typography.headlineSmall,
            color = AlertDialogDefaults.titleContentColor,
            text = title
         )
      },
      text = {
         Text(
            modifier = Modifier
               .fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            color = AlertDialogDefaults.textContentColor,
            text = message
         )
      }
   )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmAlertDialogPreview() {
   Surface {
      Box(
         modifier = Modifier.fillMaxSize(),
         contentAlignment = Alignment.Center
      ) {

      }
   }
}