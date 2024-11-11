package com.wahyusembiring.homework

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.common.util.getNotificationReminderPermission
import com.wahyusembiring.ui.component.button.AddAttachmentButton
import com.wahyusembiring.ui.component.button.AddDateButton
import com.wahyusembiring.ui.component.button.AddReminderButton
import com.wahyusembiring.ui.component.button.AddSubjectButton
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.picker.attachmentpicker.AttachmentPicker
import com.wahyusembiring.ui.component.popup.picker.datepicker.DatePicker
import com.wahyusembiring.ui.component.popup.picker.subjectpicker.SubjectPicker
import com.wahyusembiring.ui.component.popup.picker.timepicker.TimePicker
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.checkForPermissionOrLaunchPermissionLauncher
import java.lang.Error
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHomeworkScreen(
    viewModel: CreateHomeworkScreenViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectAsOneTimeEvent(viewModel.navigationEvent) { event ->
        when (event) {
            CreateHomeworkScreenNavigationEvent.NavigateBack -> {
                navController.navigateUp()
            }
            CreateHomeworkScreenNavigationEvent.NavigateToCreateSubject -> {
                navController.navigate(Screen.CreateSubject())
            }
        }
    }

    CreateHomeworkScreen(
        modifier = Modifier,
        state = state,
        onUIEvent = viewModel::onUIEvent
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is CreateHomeworkScreenPopUp.AttachmentPicker -> {
                AttachmentPicker(
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onAttachmentsConfirmed = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnAttachmentPicked(it))
                    }
                )
            }
            is CreateHomeworkScreenPopUp.DatePicker -> {
                DatePicker(
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateSelected = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDatePicked(it))
                    }
                )
            }
            is CreateHomeworkScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage.asString(),
                    buttonText = stringResource(R.string.ok),
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onButtonClicked = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            CreateHomeworkScreenPopUp.HomeworkSavedDialog -> {
                InformationAlertDialog(
                    title = stringResource(id = R.string.success),
                    message = stringResource(id = R.string.homework_saved),
                    buttonText = stringResource(id = R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnHomeworkSavedButtonClicked)
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnHomeworkSavedButtonClicked)
                    },
                )
            }
            CreateHomeworkScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }
            CreateHomeworkScreenPopUp.SaveConfirmationDialog -> {
                ConfirmationAlertDialog(
                    title = stringResource(id = R.string.save_homework),
                    message = stringResource(id = R.string.are_you_sure_you_want_to_save_this_homework),
                    positiveButtonText = stringResource(id = R.string.save),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnConfirmSaveHomeworkClick)
                    },
                    negativeButtonText = stringResource(id = R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            CreateHomeworkScreenPopUp.SubjectPicker -> {
                SubjectPicker(
                    subjects = state.subjects,
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onSubjectSelected = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnSubjectPicked(it))
                    },
                    navigateToCreateSubjectScreen = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnNavigateToSubjectScreenRequest)
                    }
                )
            }
            CreateHomeworkScreenPopUp.TimePicker -> {
                TimePicker(
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onTimeSelected = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnTimePicked(it))
                    }
                )
            }
        }
    }

}

@Composable
private fun CreateHomeworkScreen(
    modifier: Modifier = Modifier,
    state: CreateHomeworkScreenUIState,
    onUIEvent: (CreateHomeworkUIEvent) -> Unit,
) {
    val context = LocalContext.current

    val notificationPermissionRequestLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                Log.d("PermissionCheck", "All permissions granted, launching event")
                onUIEvent(CreateHomeworkUIEvent.OnPickTimeButtonClicked)
            } else {
                Log.d("PermissionCheck", "Permission denied")
            }
        }

    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            NavigationAndActionButtonHeader(
                onNavigationButtonClicked = {
                    onUIEvent(CreateHomeworkUIEvent.OnNavigateBackButtonClick)
                },
                onActionButtonClicked = {
                    onUIEvent(CreateHomeworkUIEvent.OnSaveHomeworkButtonClicked)
                },
                actionButtonText = if (state.isEditMode) {
                    stringResource(R.string.edit)
                } else {
                    stringResource(R.string.save)
                },
                navigationButtonDescription = stringResource(R.string.close_add_homework_sheet)
            )
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.Medium)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.task_title))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_title),
                            contentDescription = stringResource(R.string.homework_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    value = state.homeworkTitle,
                    onValueChange = { onUIEvent(CreateHomeworkUIEvent.OnHomeworkTitleChanged(it)) },
                )
                AddDateButton(
                    date = state.date,
                    onClicked = {
                        onUIEvent(CreateHomeworkUIEvent.OnPickDateButtonClicked)
                    }
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
                                onUIEvent(CreateHomeworkUIEvent.OnPickTimeButtonClicked)
                            },
                        )
                    }
                )
                AddSubjectButton(
                    subject = state.subject,
                    onClicked = { onUIEvent(CreateHomeworkUIEvent.OnPickSubjectButtonClicked) }
                )
//                AddAttachmentButton(
//                    attachments = state.attachments,
//                    onClicked = { onUIEvent(CreateHomeworkUIEvent.OnPickAttachmentButtonClicked) }
//                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.homework_description))
                    },
//                    leadingIcon = {
//                        Icon(
//                            painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_title),
//                            contentDescription = stringResource(R.string.homework_description),
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    },
                    value = state.description,
                    onValueChange = {
                        onUIEvent(CreateHomeworkUIEvent.OnExamDescriptionChanged(it))
                    }
                )
            }
        }
    }


}