package com.wahyusembiring.subject.screen.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.component.button.ChooseColorButton
import com.wahyusembiring.ui.component.dropdown.Dropdown
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.success.SuccessAlertDialog
import com.wahyusembiring.ui.component.v2.colorpicker.ColorPicker
import com.wahyusembiring.ui.component.v2.lecturerpicker.LecturerPicker
import com.wahyusembiring.ui.component.v2.lecturerpicker.LecturerPickerButton
import com.wahyusembiring.ui.component.v2.list.EmptyItems
import com.wahyusembiring.ui.component.v2.list.ListItem
import com.wahyusembiring.ui.component.v2.list.VerticalListContainer
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.UIText
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Composable
fun CreateSubjectScreen(
    viewModel: CreateSubjectViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectAsOneTimeEvent(viewModel.oneTimeEvent) { event ->
        when (event) {
            is CreateSubjectScreenOneTimeEvent.NavigateToCreateLecturer -> {
                navController.navigate(Screen.AddLecturer())
            }
            is CreateSubjectScreenOneTimeEvent.NavigateUp -> {
                navController.navigateUp()
            }
        }
    }

    CreateSubjectScreen(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popup in state.popUps) {
        when (popup) {
            is CreateSubjectScreenPopUp.ColorPicker -> {
                ColorPicker(
                    initialColor = state.color,
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    },
                    onColorConfirmed = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnColorPicked(it))
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    }
                )
            }
            is CreateSubjectScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popup.errorMessage.asString(),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    }
                )
            }
            is CreateSubjectScreenPopUp.LecturerPicker -> {
                LecturerPicker(
                    lecturers = state.lecturers,
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    },
                    onAddNewLecturerButtonClick = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnAddNewLecturerButtonClicked)
                    },
                    onLecturerSelected = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnLecturerSelected(it))
                    }
                )
            }
            CreateSubjectScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }
            CreateSubjectScreenPopUp.SaveConfirmation -> {
                ConfirmationAlertDialog(
                    title = stringResource(R.string.save_subject),
                    message = stringResource(R.string.are_you_sure_you_want_to_save_this_subject),
                    positiveButtonText = stringResource(R.string.save),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnSaveConfirmationDialogConfirm)
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    },
                    negativeButtonText = stringResource(R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    }
                )
            }
            CreateSubjectScreenPopUp.SubjectSaved -> {
                SuccessAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.subject_saved),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                        navController.navigateUp()
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateSubjectScreenUIEvent.OnDismissPopup(popup))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSubjectScreen(
    modifier: Modifier = Modifier,
    state: CreateSubjectScreenUIState,
    onUIEvent: (CreateSubjectScreenUIEvent) -> Unit,
) {

    val mainContainerScrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnNavigateUpButtonClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnSaveButtonClicked)
                        }
                    ) {
                        Text(
                            text = if (state.isEditMode) {
                                stringResource(R.string.edit)
                            } else {
                                stringResource(R.string.save)
                            }
                        )
                    }
                }
            )
        }
    ) { scaffoldPadding ->

        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
                .verticalScroll(mainContainerScrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            0.2f to Color.Transparent,
                            0.6f to state.color.copy(alpha = 0.2f),
                            1f to state.color.copy(alpha = 0.75f)
                        )
                    )
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.Small)
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.surface
                )
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.Large)
                ) {
                    Text(
                        text = if (state.isEditMode) {
                            stringResource(R.string.edit)
                        } else {
                            stringResource(R.string.create)
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = stringResource(R.string.subject),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.Small)
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.surface
                )
            }
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.Large)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.subject_name),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        value = state.name,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_subjects),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        },
                        onValueChange = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnSubjectNameChanged(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_enter_subject_name),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                Column {
                    Text(
                        text = stringResource(R.string.room),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        value = state.room,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_location),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        },
                        onValueChange = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnRoomChanged(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_enter_subject_name),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                Column {
                    Text(
                        text = stringResource(R.string.lecturer),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    LecturerPickerButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        lecturer = state.lecturer,
                        onClick = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnPickLecturerButtonClicked)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                Column {
                    Text(
                        text = stringResource(R.string.color),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    Button(
                        modifier = Modifier
                            .padding(start = MaterialTheme.spacing.Small),
                        colors = ButtonDefaults.buttonColors().copy(containerColor = state.color),
                        onClick = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnPickColorButtonClicked)
                        }
                    ) {
                        Text("Pick color")
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    Text(
                        text = stringResource(R.string.description),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.add_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = state.description,
                        onValueChange = {
                            onUIEvent(CreateSubjectScreenUIEvent.OnDescriptionChanged(it))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        minLines = 4,
                        maxLines = 4
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
            }
        }
    }
}