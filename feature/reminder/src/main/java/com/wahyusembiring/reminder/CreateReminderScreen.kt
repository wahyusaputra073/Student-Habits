package com.wahyusembiring.reminder

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.wahyusembiring.common.util.getNotificationReminderPermission
import com.wahyusembiring.ui.component.button.AddAttachmentButton
import com.wahyusembiring.ui.component.button.AddDateButton
import com.wahyusembiring.ui.component.button.AddReminderButton
import com.wahyusembiring.ui.component.button.ChooseColorButton
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.picker.attachmentpicker.AttachmentPicker
import com.wahyusembiring.ui.component.popup.picker.colorpicker.ColorPicker
import com.wahyusembiring.ui.component.popup.picker.datepicker.DatePicker
import com.wahyusembiring.ui.component.popup.picker.timepicker.TimePicker
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.checkForPermissionOrLaunchPermissionLauncher

@Composable
fun CreateReminderScreen(
    viewModel: CreateReminderScreenViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()

    CreateReminderScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onNavigateUp = {
            navController.navigateUp()
        }
    )
}

@Suppress("t")
@Composable
private fun CreateReminderScreen(
    state: CreateReminderScreenUIState,
    onUIEvent: (CreateReminderScreenUIEvent) -> Unit,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current

    val notificationPermissionRequestLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Log.d("PermissionCheck", "All permissions granted, launching event")
                onUIEvent(CreateReminderScreenUIEvent.OnTimePickerButtonClick)
            } else {
                Log.d("PermissionCheck", "Permission denied")
            }
        }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            NavigationAndActionButtonHeader(
                onNavigationButtonClicked = onNavigateUp,
                actionButtonText = if (state.isEditMode) {
                    stringResource(R.string.edit)
                } else {
                    stringResource(R.string.save)
                },
                onActionButtonClicked = {
                    onUIEvent(CreateReminderScreenUIEvent.OnSaveButtonClicked)
                },
                navigationButtonDescription = stringResource(R.string.close_create_reminder_sheet)
            )
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.Medium)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.reminder_title))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_title),
                            contentDescription = stringResource(R.string.reminder_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    value = state.title,
                    onValueChange = { onUIEvent(CreateReminderScreenUIEvent.OnTitleChanged(it)) },
                )
                AddDateButton(
                    date = state.date,
                    onClicked = { onUIEvent(CreateReminderScreenUIEvent.OnDatePickerButtonClick) }
                )
                AddReminderButton(
                    time = state.time,
                    onClicked = {
                        // Delete this
                        Log.d("ButtonClick", "AddReminderButton clicked")
                        onUIEvent(CreateReminderScreenUIEvent.OnTimePickerButtonClick)
//                        checkForPermissionOrLaunchPermissionLauncher(
//                            context = context,
//                            permissionToRequest = getNotificationReminderPermission(),
//                            permissionRequestLauncher = notificationPermissionRequestLauncher,
//                            onPermissionAlreadyGranted = {
//                                onUIEvent(CreateReminderScreenUIEvent.OnTimePickerButtonClick)
//                            }
//                        )
                    }
                )
                ChooseColorButton(
                    color = state.color,
                    onClick = { onUIEvent(CreateReminderScreenUIEvent.OnColorPickerButtonClick) }
                )
//                AddAttachmentButton(
//                    attachments = state.attachments,
//                    onClicked = { onUIEvent(CreateReminderScreenUIEvent.OnAttachmentPickerButtonClick) }
//                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.reminder_description))
                    },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(
//                                id = com.wahyusembiring.ui.R.drawable.ic_title
//                            ),
//                            contentDescription = stringResource(R.string.reminder_description),
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    },
                    value = state.description,
                    onValueChange = {
                        onUIEvent(CreateReminderScreenUIEvent.OnReminderDescriptionChanged(it))
                    },
                )
            }
        }
    }

    if (state.showDatePicker) {
        DatePicker(
            onDismissRequest = { onUIEvent(CreateReminderScreenUIEvent.OnDatePickerDismiss) },
            onDateSelected = { onUIEvent(CreateReminderScreenUIEvent.OnDatePicked(it)) }
        )
    }

    if (state.showTimePicker) {
        TimePicker(
            onDismissRequest = { onUIEvent(CreateReminderScreenUIEvent.OnTimePickerDismiss) },
            onTimeSelected = { onUIEvent(CreateReminderScreenUIEvent.OnTimePicked(it)) }
        )
    }

    if (state.showColorPicker) {
        ColorPicker(
            initialColor = state.color,
            onDismissRequest = { onUIEvent(CreateReminderScreenUIEvent.OnColorPickerDismiss) },
            onColorConfirmed = { onUIEvent(CreateReminderScreenUIEvent.OnColorPicked(it)) }
        )
    }

    if (state.showAttachmentPicker) {
        AttachmentPicker(
            onDismissRequest = { onUIEvent(CreateReminderScreenUIEvent.OnAttachmentPickerDismiss) },
            onAttachmentsConfirmed = { onUIEvent(CreateReminderScreenUIEvent.OnAttachmentPicked(it)) }
        )
    }

    if (state.showSavingLoading) {
        LoadingAlertDialog(message = stringResource(R.string.saving))
    }

    if (state.showSaveConfirmationDialog) {
        ConfirmationAlertDialog(
            title = stringResource(R.string.save_reminder),
            message = if (state.isEditMode) {
                stringResource(R.string.are_you_sure_you_want_to_edit_this_reminder)
            } else {
                stringResource(R.string.are_you_sure_you_want_to_save_this_reminder)
            },
            positiveButtonText = stringResource(R.string.save),
            onPositiveButtonClick = {
                onUIEvent(CreateReminderScreenUIEvent.OnSaveReminderConfirmClick)
                onUIEvent(CreateReminderScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
            negativeButtonText = stringResource(R.string.cancel),
            onNegativeButtonClick = {
                onUIEvent(CreateReminderScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(CreateReminderScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
        )
    }

    if (state.showReminderSavedDialog) {
        InformationAlertDialog(
            title = stringResource(R.string.success),
            message = stringResource(R.string.reminder_saved),
            buttonText = stringResource(R.string.ok),
            onButtonClicked = {
                onUIEvent(CreateReminderScreenUIEvent.OnReminderSavedDialogDismiss)
                onNavigateUp()
            },
            onDismissRequest = {
                onUIEvent(CreateReminderScreenUIEvent.OnReminderSavedDialogDismiss)
                onNavigateUp()
            },
        )
    }

    if (state.errorMessage != null) {
        ErrorAlertDialog(
            message = state.errorMessage.asString(),
            buttonText = stringResource(R.string.ok),
            onButtonClicked = {
                onUIEvent(CreateReminderScreenUIEvent.OnErrorDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(CreateReminderScreenUIEvent.OnErrorDialogDismiss)
            }
        )
    }

}