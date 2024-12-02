package com.wahyusembiring.ui.component.popup.alertdialog.loading

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.wahyusembiring.ui.R
import com.wahyusembiring.ui.theme.spacing

@Composable
fun LoadingAlertDialog(
   message: String
) {
   val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
   val progress by animateLottieCompositionAsState(
      composition = composition,
      iterations = LottieConstants.IterateForever,
      reverseOnRepeat = false
   )

   Dialog(
      onDismissRequest = {},
   ) {
      Column(
         verticalArrangement = Arrangement.Center,
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         LottieAnimation(
            modifier = Modifier.size(150.dp),
            composition = composition,
            progress = { progress }
         )
         Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
         Text(
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.Medium),
            text = message
         )
      }
   }
}

@Preview(showBackground = true)
@Composable
private fun LoadingAlertDialogPreview() {
   LoadingAlertDialog(message = "Loading...")
}