package com.wahyusembiring.ui.component.popup.alertdialog.confirmation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing

@Composable
fun ConfirmationAlertDialog(
   onPositiveButtonClick: () -> Unit,
   onNegativeButtonClick: () -> Unit,
   title: String,
   message: String,
   positiveButtonText: String,
   negativeButtonText: String,
   onDismissRequest: () -> Unit,
) {
   val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.thinking))
   val progress by animateLottieCompositionAsState(
      composition = composition,
      iterations = LottieConstants.IterateForever,
      reverseOnRepeat = true
   )
   var showLottie by remember { mutableStateOf(false) }

   LaunchedEffect(Unit) {
      showLottie = true
   }

   Dialog(
      onDismissRequest = onDismissRequest
   ) {
      Surface(
         shape = MaterialTheme.shapes.large
      ) {
         Column(
            modifier = Modifier
               .padding(horizontal = MaterialTheme.spacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
         ) {
            AnimatedVisibility(
               visible = showLottie,
               enter = scaleIn(
                  animationSpec = spring(
                     dampingRatio = Spring.DampingRatioLowBouncy,
                     stiffness = Spring.StiffnessLow
                  )
               )
            ) {
               LottieAnimation(
                  modifier = Modifier.height(150.dp),
                  composition = composition,
                  progress = { progress }
               )
            }
            Text(
               modifier = Modifier.padding(bottom = MaterialTheme.spacing.Small),
               style = MaterialTheme.typography.titleMedium,
               color = MaterialTheme.colorScheme.primary,
               text = title
            )
            Text(
               modifier = Modifier,
               style = MaterialTheme.typography.bodyMedium,
               color = AlertDialogDefaults.textContentColor,
               text = message
            )
            Row(
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(MaterialTheme.spacing.Small),
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.End
            ) {
               TextButton(onClick = onNegativeButtonClick) {
                  Text(text = negativeButtonText)
               }
               TextButton(onClick = onPositiveButtonClick) {
                  Text(text = positiveButtonText)
               }
            }
         }
      }
   }
}

@Preview(showBackground = true)
@Composable
private fun ConfirmAlertDialogPreview() {
   HabitTheme {
      ConfirmationAlertDialog(
         title = "Title",
         message = "Message",
         positiveButtonText = "Ok",
         onPositiveButtonClick = {},
         negativeButtonText = "Cancel",
         onNegativeButtonClick = {},
         onDismissRequest = {}
      )
   }
}