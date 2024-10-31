package com.wahyusembiring.ui.component.popup.alertdialog.information

data class InformationAlertDialogState(
   val title: String = "",
   val message: String = "",
   val buttonText: String = "",
   val onButtonClicked: () -> Unit = {}
)
