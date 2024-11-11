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
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamScreenViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()

    CollectAsOneTimeEvent(viewModel.navigationEvent) { event ->
        when (event) {
            CreateExamScreenNavigationEvent.NavigateBack -> {
                navController.navigateUp()
            }
            CreateExamScreenNavigationEvent.NavigateToCreateSubject -> {
                navController.navigate(Screen.CreateSubject())
            }
        }
    }

    ExamScreenUI(
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is CreateExamScreenPopUp.AttachmentPicker -> {
                AttachmentPicker(
                    onAttachmentsConfirmed = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnAttachmentPicked(it))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    }
                )
            }
            is CreateExamScreenPopUp.DatePicker -> {
                DatePicker(
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateSelected = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDatePicked(it))
                    }
                )
            }
            is CreateExamScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage.asString(),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    }
                )
            }
            is CreateExamScreenPopUp.ExamSavedDialog -> {
                InformationAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.exam_saved),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnNavigateBackRequest)
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnNavigateBackRequest)
                    },
                )
            }
            is CreateExamScreenPopUp.Loading -> {
                LoadingAlertDialog(message = stringResource(R.string.loading))
            }
            is CreateExamScreenPopUp.SaveConfirmationDialog -> {
                ConfirmationAlertDialog(
                    title = stringResource(R.string.save_exam),
                    message = if (state.isEditMode) {
                        stringResource(R.string.are_you_sure_you_want_to_edit_this_exam)
                    } else {
                        stringResource(R.string.are_you_sure_you_want_to_save_this_exam)
                    },
                    positiveButtonText = stringResource(R.string.save),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnSaveExamConfirmClick)
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    negativeButtonText = stringResource(R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            is CreateExamScreenPopUp.SubjectPicker -> {
                SubjectPicker(
                    subjects = state.subjects,
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onSubjectSelected = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnSubjectPicked(it))
                    },
                    navigateToCreateSubjectScreen = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnNavigateToSubjectScreenRequest)
                    }
                )
            }
            is CreateExamScreenPopUp.TimePicker -> {
                TimePicker(
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onTimeSelected = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnTimePicked(it))
                    }
                )
            }

            CreateExamScreenPopUp.ExamCategoryPicker -> {
                ExamCategoryPicker(
                    initialCategory = state.category,
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onCategoryPicked = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnCategoryPicked(it))
                    }
                )
            }
        }
    }

}

@Suppress("t")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamScreenUI(
    state: ExamScreenUIState,
    onUIEvent: (ExamScreenUIEvent) -> Unit,
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
                onNavigationButtonClicked = {
                    onUIEvent(ExamScreenUIEvent.OnNavigateBackRequest)
                },
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
                        onUIEvent(ExamScreenUIEvent.OnExamTimePickerClick)
//                        checkForPermissionOrLaunchPermissionLauncher(
//                            context = context,
//                            permissionToRequest = getNotificationReminderPermission(),
//                            permissionRequestLauncher = notificationPermissionRequestLauncher,
//                            onPermissionAlreadyGranted = {
//                                Log.d(
//                                    "PermissionCheck",
//                                    "Permission already granted, launching event"
//                                )
//                                onUIEvent(ExamScreenUIEvent.OnExamTimePickerClick)
//                            }
//                        )
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
//                AddAttachmentButton(
//                    attachments = state.attachments,
//                    onClicked = { onUIEvent(ExamScreenUIEvent.OnExamAttachmentPickerClick) }
//                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.exam_description))
                    },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_title),
//                            contentDescription = stringResource(R.string.exam_description),
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    },
                    value = state.description,
                    onValueChange = {
                        onUIEvent(ExamScreenUIEvent.OnExamDescriptionChanged(it))
                    },
                )

            }
        }
    }

}