package com.wahyusembiring.ui.component.popup.alertdialog.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wahyusembiring.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingAlertDialog(
   message: String
) {
   BasicAlertDialog(onDismissRequest = { }) {
      Column(
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         CircularProgressIndicator()
         Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
         Text(text = message)
      }
   }
}

@Preview(showBackground = true)
@Composable
private fun LoadingAlertDialogPreview() {
   Surface(
      modifier = Modifier.fillMaxSize(),
      color = MaterialTheme.colorScheme.background
   ) {
      Box(
         contentAlignment = Alignment.Center
      ) {

      }
   }
}