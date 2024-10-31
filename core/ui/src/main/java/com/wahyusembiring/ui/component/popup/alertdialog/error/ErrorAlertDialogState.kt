package com.wahyusembiring.ui.component.popup.alertdialog.error

data class ErrorAlertDialogState(
   val message: String = "",
   val buttonText: String = "",
   val onButtonClicked: () -> Unit = {},
)
