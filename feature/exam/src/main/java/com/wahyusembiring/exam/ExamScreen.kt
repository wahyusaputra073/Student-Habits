package com.wahyusembiring.exam

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.getNotificationReminderPermission
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.ui.component.button.AddAttachmentButton
import com.wahyusembiring.ui.component.button.AddDateButton
import com.wahyusembiring.ui.component.button.AddReminderButton
import com.wahyusembiring.ui.component.button.AddSubjectButton
import com.wahyusembiring.ui.component.button.ExamCategoryPickerButton
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.picker.attachmentpicker.AttachmentPicker
import com.wahyusembiring.ui.component.popup.picker.datepicker.DatePicker
import com.wahyusembiring.ui.component.popup.picker.examcategorypicker.ExamCategoryPicker
import com.wahyusembiring.ui.component.popup.picker.subjectpicker.SubjectPicker
import com.wahyusembiring.ui.component.popup.picker.timepicker.TimePicker
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.checkForPermissionOrLaunchPermissionLauncher

@Composable
fun ExamScreen(
    viewModel: ExamScreenViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()
    ExamScreenUI(
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onNavigateBack = {
            navController.navigateUp()
        },
        onNavigateToCreateSubjectScreen = {
            navController.navigate(Screen.CreateSubject())
        }
    )
}

@Suppress("t")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamScreenUI(
    state: ExamScreenUIState,
    onUIEvent: (ExamScreenUIEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCreateSubjectScreen: () -> Unit,
) {
    val context = LocalContext.current
    val notificationPermissionRequestLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Log.d("PermissionCheck", "All permissions granted, launching event")
                onUIEvent(ExamScreenUIEvent.OnExamTimePickerClick)
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
                onNavigationButtonClicked = onNavigateBack,
                actionButtonText = if (state.isEditMode) {
                    stringResource(R.string.edit)
                } else {
                    stringResource(R.string.save)
                },
                onActionButtonClicked = { onUIEvent(ExamScreenUIEvent.OnSaveExamButtonClick) },
                navigationButtonDescription = stringResource(R.string.close_add_exam_sheet)
            )
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.Medium)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.exam_name))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_title),
                            contentDescription = stringResource(R.string.exam_name),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    value = state.name,
                    onValueChange = { onUIEvent(ExamScreenUIEvent.OnExamNameChanged(it)) },
                )
                AddDateButton(
                    date = state.date,
                    onClicked = { onUIEvent(ExamScreenUIEvent.OnExamDatePickerClick) }
                )
                AddReminderButton(
                    time = state.time,
                    onClicked = {
                        Log.d("ButtonClick", "AddReminderButton clicked")
                        checkForPermissionOrLaunchPermissionLauncher(
                            context = context,
                            permissionToRequest = getNotificationReminderPermission(),
                            permissionRequestLauncher = notificationPermissionRequestLauncher,
                            onPermissionAlreadyGranted = {
                                Log.d(
                                    "PermissionCheck",
                                    "Permission already granted, launching event"
                                )
                                onUIEvent(ExamScreenUIEvent.OnExamTimePickerClick)
                            }
                        )
                    }
                )
                ExamCategoryPickerButton(
                    examCategory = state.category,
                    onClicked = { onUIEvent(ExamScreenUIEvent.OnExamCategoryPickerClick) }
                )
                AddSubjectButton(
                    subject = state.subject,
                    onClicked = { onUIEvent(ExamScreenUIEvent.OnExamSubjectPickerClick) }
                )
                AddAttachmentButton(
                    attachments = state.attachments,
                    onClicked = { onUIEvent(ExamScreenUIEvent.OnExamAttachmentPickerClick) }
                )
            }
        }
    }

    if (state.showDatePicker) {
        DatePicker(
            onDismissRequest = { onUIEvent(ExamScreenUIEvent.OnDatePickedDismiss) },
            onDateSelected = { onUIEvent(ExamScreenUIEvent.OnDatePicked(it)) }
        )
    }

    if (state.showTimePicker) {
        TimePicker(
            onDismissRequest = { onUIEvent(ExamScreenUIEvent.OnTimePickedDismiss) },
            onTimeSelected = { onUIEvent(ExamScreenUIEvent.OnTimePicked(it)) }
        )
    }

    if (state.showSubjectPicker) {
        SubjectPicker(
            subjects = state.subjects,
            onDismissRequest = { onUIEvent(ExamScreenUIEvent.OnSubjectPickedDismiss) },
            onSubjectSelected = { onUIEvent(ExamScreenUIEvent.OnSubjectPicked(it)) },
            navigateToCreateSubjectScreen = onNavigateToCreateSubjectScreen
        )
    }

    if (state.showAttachmentPicker) {
        AttachmentPicker(
            onAttachmentsConfirmed = { onUIEvent(ExamScreenUIEvent.OnAttachmentPicked(it)) },
            onDismissRequest = { onUIEvent(ExamScreenUIEvent.OnAttachmentPickedDismiss) }
        )
    }

    if (state.showCategoryPicker) {
        ExamCategoryPicker(
            initialCategory = ExamCategory.WRITTEN,
            onDismissRequest = { onUIEvent(ExamScreenUIEvent.OnCategoryPickedDismiss) },
            onCategoryPicked = { onUIEvent(ExamScreenUIEvent.OnCategoryPicked(it)) }
        )
    }

    if (state.showSavingLoading) {
        LoadingAlertDialog(message = stringResource(R.string.saving))
    }

    if (state.showSaveConfirmationDialog) {
        ConfirmationAlertDialog(
            title = stringResource(R.string.save_exam),
            message = if (state.isEditMode) {
                stringResource(R.string.are_you_sure_you_want_to_edit_this_exam)
            } else {
                stringResource(R.string.are_you_sure_you_want_to_save_this_exam)
            },
            positiveButtonText = stringResource(R.string.save),
            onPositiveButtonClick = {
                onUIEvent(ExamScreenUIEvent.OnSaveExamConfirmClick)
                onUIEvent(ExamScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
            negativeButtonText = stringResource(R.string.cancel),
            onNegativeButtonClick = {
                onUIEvent(ExamScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(ExamScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
        )
    }

    if (state.showExamSavedDialog) {
        InformationAlertDialog(
            title = stringResource(R.string.success),
            message = stringResource(R.string.exam_saved),
            buttonText = stringResource(R.string.ok),
            onButtonClicked = {
                onUIEvent(ExamScreenUIEvent.OnExamSavedDialogDismiss)
                onNavigateBack()
            },
            onDismissRequest = {
                onUIEvent(ExamScreenUIEvent.OnExamSavedDialogDismiss)
                onNavigateBack()
            },
        )
    }

    if (state.errorMessage != null) {
        ErrorAlertDialog(
            message = state.errorMessage.asString(),
            buttonText = stringResource(R.string.ok),
            onButtonClicked = {
                onUIEvent(ExamScreenUIEvent.OnErrorDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(ExamScreenUIEvent.OnErrorDialogDismiss)
            }
        )
    }

}