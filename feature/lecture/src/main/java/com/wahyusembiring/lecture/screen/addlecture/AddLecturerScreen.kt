package com.wahyusembiring.lecture.screen.addlecture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.lecture.R
import com.wahyusembiring.ui.component.emailinput.EmailInput
import com.wahyusembiring.ui.component.multiaddressinput.MultiAddressInput
import com.wahyusembiring.ui.component.officehourinput.OfficeHourInput
import com.wahyusembiring.ui.component.phonenumberinput.PhoneNumberInput
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.success.SuccessAlertDialog
import com.wahyusembiring.ui.component.profilepicturepicker.ProfilePicturePicker
import com.wahyusembiring.ui.component.websiteinput.WebsiteInput
import com.wahyusembiring.ui.theme.spacing

@Composable
fun AddLectureScreen(
    viewModel: AddLecturerScreenViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectAsOneTimeEvent(viewModel.navigationEvent) {  event ->
        when (event) {
            AddLecturerScreenNavigationEvent.NavigateBack -> {
                navController.navigateUp()
            }
        }
    }

    AddLectureScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is AddLecturerScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }
            is AddLecturerScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.message.asString(),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            is AddLecturerScreenPopUp.LecturerSaved -> {
                SuccessAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.lecture_saved_successfully),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnLectureSavedOkButtonClick)
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            AddLecturerScreenPopUp.SaveLecturerConfirmationDialog -> {
                ConfirmationAlertDialog(
                    title = "Save Lecture",
                    message = "Are you sure you want to save this lecture?",
                    positiveButtonText = stringResource(R.string.save),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnSaveConfirmationDialogConfirm)
                    },
                    negativeButtonText = stringResource(R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(AddLecturerScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLectureScreen(
    state: AddLecturerScreenUIState,
    onUIEvent: (AddLecturerScreenUIEvent) -> Unit,
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onUIEvent(AddLecturerScreenUIEvent.OnBackButtonClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                onUIEvent(AddLecturerScreenUIEvent.OnSaveButtonClick)
                            }
                        ) {
                            if (state.isEditMode) {
                                Text(text = stringResource(R.string.edit))
                            } else {
                                Text(text = stringResource(R.string.save))
                            }
                        }
                    }
                }
            )
        }
    ) { scaffoldPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ProfilePicturePicker(
                        modifier = Modifier
                            .size(100.dp),
                        imageUri = state.profilePictureUri,
                        onImageSelected = {
                            onUIEvent(AddLecturerScreenUIEvent.OnProfilePictureSelected(it))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }
            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.spacing.Medium),
                    value = state.name,
                    onValueChange = {
                        onUIEvent(AddLecturerScreenUIEvent.OnLecturerNameChange(it))
                    },
                    label = {
                        Text(text = stringResource(R.string.lecture_name))
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }
            item {
                PhoneNumberInput(
                    phoneNumbers = state.phoneNumbers,
                    onNewPhoneNumber = {
                        onUIEvent(AddLecturerScreenUIEvent.OnNewPhoneNumber(it))
                    },
                    onDeletePhoneNumber = { phoneNumber ->
                        onUIEvent(AddLecturerScreenUIEvent.OnDeletePhoneNumber(phoneNumber))
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }
            item {
                EmailInput(
                    emails = state.emails,
                    onNewEmail = {
                        onUIEvent(AddLecturerScreenUIEvent.OnNewEmail(it))
                    },
                    onDeleteEmail = { email ->
                        onUIEvent(AddLecturerScreenUIEvent.OnDeleteEmail(email))
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }
            item {
                MultiAddressInput(
                    addresses = state.addresses,
                    onNewAddress = {
                        onUIEvent(AddLecturerScreenUIEvent.OnNewAddress(it))
                    },
                    onDeleteAddress = { address ->
                        onUIEvent(AddLecturerScreenUIEvent.OnDeleteAddress(address))
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }

            item {
                WebsiteInput(
                    websites = state.websites,
                    onNewWebsiteAddClick = {
                        onUIEvent(AddLecturerScreenUIEvent.OnNewWebsite(it))
                    },
                    onDeleteWebsite = { website ->
                        onUIEvent(AddLecturerScreenUIEvent.OnDeleteWebsite(website))
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
            }

            item {
                OfficeHourInput(
                    officeHours = state.officeHours,
                    onNewOfficeHour = {
                        onUIEvent(AddLecturerScreenUIEvent.OnNewOfficeHour(it))
                    },
                    onDeleteOfficeHour = { officeHour ->
                        onUIEvent(AddLecturerScreenUIEvent.OnDeleteOfficeHour(officeHour))
                    }
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }

        }
    }

}