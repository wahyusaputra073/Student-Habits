package com.wahyusembiring.reminder

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.common.util.getNotificationReminderPermission
import com.wahyusembiring.ui.ReminderOption
import com.wahyusembiring.ui.component.button.DatePickerButton
import com.wahyusembiring.ui.component.button.TimePickerButton
import com.wahyusembiring.ui.component.button.ChooseColorButton
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.success.SuccessAlertDialog
import com.wahyusembiring.ui.component.popup.picker.attachmentpicker.AttachmentPicker
import com.wahyusembiring.ui.component.v2.colorpicker.ColorPicker
import com.wahyusembiring.ui.component.popup.picker.datepicker.DatePicker
import com.wahyusembiring.ui.component.popup.picker.timepicker.TimePicker
import com.wahyusembiring.ui.component.v2.datetimepicker.DateTimePicker
import com.wahyusembiring.ui.component.v2.datetimepicker.rememberDateTimePickerState
import com.wahyusembiring.ui.component.v2.dropdownmenu.ActionDropdownIconButton
import com.wahyusembiring.ui.component.v2.list.EmptyItems
import com.wahyusembiring.ui.component.v2.list.ListItem
import com.wahyusembiring.ui.component.v2.list.VerticalListContainer
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.checkForPermissionOrLaunchPermissionLauncher
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun CreateReminderScreen(
    viewModel: CreateReminderScreenViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()

    CollectAsOneTimeEvent(viewModel.navigationEvent) { event ->
        when (event) {
            CreateReminderScreenNavigationEvent.NavigateUp -> {
                navController.navigateUp()
            }
        }
    }

    CreateReminderScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is CreateReminderScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage.asString(),
                    buttonText = stringResource(R.string.ok),
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    },
                    onButtonClicked = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    }
                )
            }
            is CreateReminderScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }
            is CreateReminderScreenPopUp.ReminderSavedDialog -> {
                SuccessAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.reminder_saved),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnReminderSavedOkButtonClick)
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    },
                )
            }
            is CreateReminderScreenPopUp.SaveConfirmationDialog -> {
                ConfirmationAlertDialog(
                    title = stringResource(R.string.save_reminder),
                    message = if (state.isEditMode) {
                        stringResource(R.string.are_you_sure_you_want_to_edit_this_reminder)
                    } else {
                        stringResource(R.string.are_you_sure_you_want_to_save_this_reminder)
                    },
                    positiveButtonText = stringResource(R.string.save),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnSaveReminderConfirmClick)
                    },
                    negativeButtonText = stringResource(R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    },
                )
            }
            is CreateReminderScreenPopUp.DateTimePicker -> {
                val dateTimePickerState = rememberDateTimePickerState()
                DateTimePicker(
                    state = dateTimePickerState,
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    },
                    onDateTimeSelected = {
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnAddReminderDate(it))
                        viewModel.onUIEvent(CreateReminderScreenUIEvent.OnPopDismiss(popUp))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateReminderScreen(
    state: CreateReminderScreenUIState,
    onUIEvent: (CreateReminderScreenUIEvent) -> Unit,
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
                            onUIEvent(CreateReminderScreenUIEvent.OnNavigateUpButtonClick)
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
                            onUIEvent(CreateReminderScreenUIEvent.OnSaveButtonClicked)
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
                            0.6f to MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            1f to MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
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
                        text = stringResource(R.string.reminder),
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
                        text = stringResource(R.string.reminder_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        value = state.title,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_title),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        },
                        onValueChange = {
                            onUIEvent(CreateReminderScreenUIEvent.OnTitleChanged(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_enter_reminder_title),
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
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    VerticalListContainer(
                        modifier = Modifier.fillMaxWidth(),
                        items = state.reminderDates,
                        title = stringResource(R.string.remind_me_at),
                        actionButton = {
                            IconButton(
                                onClick = {
                                    onUIEvent(CreateReminderScreenUIEvent.OnAddReminderDateButtonClick)
                                },
                            ) {
                                Icon(
                                    painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_add),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = null
                                )
                            }
                        },
                        ifEmpty = { EmptyItems(stringResource(R.string.please_add_a_reminder_date)) },
                    ) { reminderDate ->
                        ListItem(
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_timer),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        onUIEvent(CreateReminderScreenUIEvent.OnDeleteReminderDateButtonClick(reminderDate))
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = reminderDate.atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    Text(
                        text = stringResource(R.string.notes),
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
                                text = stringResource(R.string.add_some_notes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = state.notes,
                        onValueChange = {
                            onUIEvent(CreateReminderScreenUIEvent.OnNotesChanged(it))
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