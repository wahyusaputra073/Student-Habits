package com.wahyusembiring.ui.component.popup.alertdialog.error

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun ErrorAlertDialog(
   message: String,
   buttonText: String,
   onDismissRequest: () -> Unit,
   onButtonClicked: () -> Unit = onDismissRequest,
) {
   val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error))
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
                  modifier = Modifier.height(100.dp),
                  composition = composition,
                  progress = { progress }
               )
            }
            Text(
               modifier = Modifier.padding(bottom = MaterialTheme.spacing.Small),
               style = MaterialTheme.typography.titleMedium,
               color = MaterialTheme.colorScheme.error,
               text = stringResource(R.string.error)
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
               TextButton(onClick = onButtonClicked) {
                  Text(text = buttonText)
               }
            }
         }
      }
   }
}