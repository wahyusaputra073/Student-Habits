package com.wahyusembiring.subject.screen.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.component.button.ChooseColorButton
import com.wahyusembiring.ui.component.dropdown.Dropdown
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.picker.colorpicker.ColorPicker
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.UIText


@Composable
fun CreateSubjectScreen(
    viewModel: CreateSubjectViewModel,
    navController: NavHostController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CreateSubjectScreen(
        modifier = Modifier.fillMaxSize(),
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onNavigateUp = {
            navController.navigateUp()
        },
        onNavigateToCreateLecturer = {
            navController.navigate(Screen.AddLecturer)
        }
    )
}

@Composable
private fun CreateSubjectScreen(
    modifier: Modifier = Modifier,
    state: CreateSubjectScreenUIState,
    onUIEvent: (CreateSubjectScreenUIEvent) -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateToCreateLecturer: () -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = modifier.padding(paddingValues)
        ) {
            BackAndSaveHeader(
                onBackButtonClicked = onNavigateUp,
                onSaveButtonClicked = {
                    onUIEvent(CreateSubjectScreenUIEvent.OnSaveButtonClicked)
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.Medium)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text(text = stringResource(R.string.subject_name))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = com.wahyusembiring.ui.R.drawable.ic_title),
                            contentDescription = stringResource(R.string.subject_name),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    value = state.name,
                    onValueChange = { onUIEvent(CreateSubjectScreenUIEvent.OnSubjectNameChanged(it)) },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = stringResource(R.string.room),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    placeholder = {
                        Text(text = stringResource(R.string.room))
                    },
                    singleLine = true,
                    value = state.room,
                    onValueChange = { onUIEvent(CreateSubjectScreenUIEvent.OnRoomChanged(it)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                ChooseColorButton(
                    color = state.color,
                    onClick = { onUIEvent(CreateSubjectScreenUIEvent.OnPickColorButtonClicked) }
                )
                Dropdown(
                    items = state.lectures,
                    title = {
                        if (it?.name != null) {
                            UIText.DynamicString(it.name)
                        } else {
                            UIText.StringResource(R.string.there_are_no_lecturer_selected)
                        }
                    },
                    selected = state.lecture,
                    onItemClick = {
                        onUIEvent(CreateSubjectScreenUIEvent.OnLecturerSelected(it))
                    },
                    emptyContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.Medium),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(R.string.there_are_no_lecturer_avaliable))
                            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                            Button(
                                onClick = onNavigateToCreateLecturer
                            ) {
                                Text(text = stringResource(R.string.add_new_lecturer))
                            }
                        }
                    }
                )
            }
        }
    }
    if (state.showColorPicker) {
        ColorPicker(
            initialColor = state.color,
            onDismissRequest = { onUIEvent(CreateSubjectScreenUIEvent.OnColorPickerDismiss) },
            onColorConfirmed = { onUIEvent(CreateSubjectScreenUIEvent.OnColorPicked(it)) }
        )
    }

    if (state.showSavingLoading) {
        LoadingAlertDialog(message = stringResource(R.string.saving))
    }

    if (state.showSaveConfirmationDialog) {
        ConfirmationAlertDialog(
            title = stringResource(R.string.save_subject),
            message = stringResource(R.string.are_you_sure_you_want_to_save_this_subject),
            positiveButtonText = stringResource(R.string.save),
            onPositiveButtonClick = {
                onUIEvent(CreateSubjectScreenUIEvent.OnSaveConfirmationDialogConfirm)
            },
            negativeButtonText = stringResource(R.string.cancel),
            onNegativeButtonClick = {
                onUIEvent(CreateSubjectScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(CreateSubjectScreenUIEvent.OnSaveConfirmationDialogDismiss)
            },
        )
    }

    if (state.showSubjectSavedDialog) {
        InformationAlertDialog(
            title = stringResource(R.string.success),
            message = stringResource(R.string.subject_saved),
            buttonText = stringResource(R.string.ok),
            onButtonClicked = {
                onUIEvent(CreateSubjectScreenUIEvent.OnSubjectSavedDialogDismiss)
                onNavigateUp()
            },
            onDismissRequest = {
                onUIEvent(CreateSubjectScreenUIEvent.OnSubjectSavedDialogDismiss)
                onNavigateUp()
            },

            )
    }
}

@Composable
private fun BackAndSaveHeader(
    onBackButtonClicked: () -> Unit,
    onSaveButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackButtonClicked
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back_arrow),
                contentDescription = stringResource(R.string.back)
            )
        }
        Button(
            modifier = Modifier.padding(end = MaterialTheme.spacing.Medium),
            onClick = onSaveButtonClicked
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}
