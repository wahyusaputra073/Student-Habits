package com.wahyusembiring.ui.component.popup.alertdialog.confirmation

data class ConfirmationAlertDialogState(
   val title: String = "",
   val message: String = "",
   val positiveButtonText: String = "",
   val negativeButtonText: String = "",
   val onPositiveButtonClicked: () -> Unit = {},
   val onNegativeButtonClicked: () -> Unit = {},
)