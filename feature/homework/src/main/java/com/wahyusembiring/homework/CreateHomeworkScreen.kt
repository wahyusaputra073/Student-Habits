package com.wahyusembiring.homework

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.ui.ReminderOption
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.success.SuccessAlertDialog
import com.wahyusembiring.ui.component.popup.picker.attachmentpicker.AttachmentPicker
import com.wahyusembiring.ui.component.popup.picker.subjectpicker.SubjectPicker
import com.wahyusembiring.ui.component.v2.datetimepicker.DateTimePicker
import com.wahyusembiring.ui.component.v2.datetimepicker.DateTimePickerRange
import com.wahyusembiring.ui.component.v2.datetimepicker.rememberDateTimePickerRangeState
import com.wahyusembiring.ui.component.v2.datetimepicker.rememberDateTimePickerState
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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
            is CreateHomeworkScreenPopUp.DuePeriodPicker -> {
                val rangeDateTimePickerState = rememberDateTimePickerRangeState()

                DateTimePickerRange(
                    state = rangeDateTimePickerState,
                    onCanceled = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateSelected = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDueDateChanged(it.start))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDeadlineDateChanged(it.endInclusive))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
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
                SuccessAlertDialog(
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
            CreateHomeworkScreenPopUp.DueReminderPicker -> {
                val dateTimePickerState = rememberDateTimePickerState(state.dueDate)
                DateTimePicker(
                    state = dateTimePickerState,
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateTimeSelected = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDueReminderChanged(ReminderOption.Custom(it)))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    allowedDateTime = { it.isBefore(state.dueDate) }
                )
            }
            CreateHomeworkScreenPopUp.DeadlineReminderPicker -> {
                val dateTimePickerState = rememberDateTimePickerState(state.deadline)
                DateTimePicker(
                    state = dateTimePickerState,
                    onDismissRequest = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateTimeSelected = {
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDeadlineReminderChanged(ReminderOption.Custom(it)))
                        viewModel.onUIEvent(CreateHomeworkUIEvent.OnDismissPopUp(popUp))
                    },
                    allowedDateTime = { it.isBefore(state.deadline) }
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateHomeworkScreen(
    modifier: Modifier = Modifier,
    state: CreateHomeworkScreenUIState,
    onUIEvent: (CreateHomeworkUIEvent) -> Unit,
) {

    val mainContainerScrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localDensity = LocalDensity.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onUIEvent(CreateHomeworkUIEvent.OnNavigateBackButtonClick)
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
                            onUIEvent(CreateHomeworkUIEvent.OnSaveHomeworkButtonClicked)
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
            modifier = modifier
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
                        text = stringResource(R.string.task),
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

                    if (state.isEditMode) {
                        Row(
                            modifier = Modifier
                                .clickable(
                                    role = Role.Checkbox,
                                    onClick = {
                                        onUIEvent(CreateHomeworkUIEvent.OnTaskCompletedStatusChanged(!state.isCompleted))
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.isCompleted,
                                onCheckedChange = null
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = MaterialTheme.spacing.Small),
                                text = stringResource(R.string.mark_task_as_completed),
                                color = if (state.isCompleted) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                    }
                    Text(
                        text = stringResource(R.string.task_name),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        value = state.homeworkTitle,
                        textStyle = if (state.isCompleted) {
                            LocalTextStyle.current.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        } else LocalTextStyle.current,
                        leadingIcon = if (state.isCompleted) {
                            {
                                AnimatedVisibility(
                                    visible = state.isCompleted,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                            }
                        } else null,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_title),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        },
                        onValueChange = {
                            onUIEvent(CreateHomeworkUIEvent.OnHomeworkTitleChanged(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_enter_name_of_task),
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
                    Text(
                        text = stringResource(R.string.due_period),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small)
                            .pointerInput(state.dueDate, state.deadline) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent =
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        onUIEvent(CreateHomeworkUIEvent.OnPickDuePeriodButtonClicked)
                                    }
                                }
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_select_a_date_range),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = buildString {
                            append(state.dueDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())))
                            append(" - ")
                            append(state.deadline.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())))
                        },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_calendar),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Unspecified
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    Text(
                        text = stringResource(R.string.subject),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small)
                            .pointerInput(state.subject) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent =
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        onUIEvent(CreateHomeworkUIEvent.OnPickSubjectButtonClicked)
                                    }
                                }
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_select_a_subject),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = state.subject?.name ?: "",
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_dropdown),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_subjects),
                                contentDescription = null,
                                tint = state.subject?.color ?: MaterialTheme.colorScheme.primary
                            )
                        },
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Unspecified
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {

                    var showDropdown by remember { mutableStateOf(false) }
                    var dueReminderContainerWidth by remember { mutableStateOf(Dp.Unspecified) }
                    var visible by remember { mutableStateOf(true) }

                    Row(
                        modifier = Modifier
                            .clickable(
                                role = Role.Checkbox,
                                onClick = {
                                    visible = !visible
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = visible,
                            onCheckedChange = null
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = MaterialTheme.spacing.Small),
                            text = stringResource(R.string.notify_me_before_the_due_date),
                            color = if (visible) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    AnimatedVisibility(
                        visible = visible,
                    ) {
                        Column(
                            modifier = Modifier
                                .onSizeChanged {
                                    with(localDensity) {
                                        dueReminderContainerWidth = (it.width / density).toInt().dp
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.spacing.Small)
                                    .pointerInput(state.subject) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent =
                                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDropdown = true
                                            }
                                        }
                                    },
                                value = when (val reminder = state.dueReminder) {
                                    is ReminderOption.Predefined -> reminder.displayName.asString()
                                    is ReminderOption.Custom -> reminder.dateTime.format(
                                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(Locale.getDefault())
                                    )
                                    null -> ""
                                },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_dropdown),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_reminder),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onValueChange = {},
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Unspecified
                                ),
                                singleLine = true,
                                maxLines = 1,
                            )
                            DropdownMenu(
                                modifier = Modifier
                                    .width(dueReminderContainerWidth),
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                for (option in ReminderOption.dueReminderDefaultOptions) {
                                    DropdownMenuItem(
                                        contentPadding = PaddingValues(
                                            horizontal = MaterialTheme.spacing.Large,
                                            vertical = 0.dp
                                        ),
                                        text = { Text(text = option.displayName.asString()) },
                                        onClick = {
                                            onUIEvent(CreateHomeworkUIEvent.OnDueReminderChanged(option))
                                            showDropdown = false
                                        }
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = MaterialTheme.spacing.Large,
                                        vertical = 0.dp
                                    ),
                                    text = { Text(text = stringResource(R.string.custom_triple_dot)) },
                                    onClick = {
                                        onUIEvent(CreateHomeworkUIEvent.OnCustomDueReminderButtonClicked)
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {

                    var showDropdown by remember { mutableStateOf(false) }
                    var deadlineReminderContainerWidth by remember { mutableStateOf(Dp.Unspecified) }
                    var visible by remember { mutableStateOf(true) }

                    Row(
                        modifier = Modifier
                            .clickable(
                                role = Role.Checkbox,
                                onClick = {
                                    visible = !visible
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = visible,
                            onCheckedChange = null
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = MaterialTheme.spacing.Small),
                            text = stringResource(R.string.notify_me_before_the_deadline),
                            color = if (visible) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    AnimatedVisibility(
                        visible = visible,
                    ) {
                        Column(
                            modifier = Modifier
                                .onSizeChanged {
                                    with(localDensity) {
                                        deadlineReminderContainerWidth = (it.width / density).toInt().dp
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.spacing.Small)
                                    .pointerInput(state.subject) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent =
                                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDropdown = true
                                            }
                                        }
                                    },
                                value = when (val reminder = state.deadlineReminder) {
                                    is ReminderOption.Predefined -> reminder.displayName.asString()
                                    is ReminderOption.Custom -> reminder.dateTime.format(
                                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(Locale.getDefault())
                                    )
                                    null -> ""
                                },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_dropdown),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_reminder),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onValueChange = {},
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Unspecified
                                ),
                                singleLine = true,
                                maxLines = 1,
                            )
                            DropdownMenu(
                                modifier = Modifier
                                    .width(deadlineReminderContainerWidth),
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                for (option in ReminderOption.deadlineReminderDefaultOptions) {
                                    DropdownMenuItem(
                                        contentPadding = PaddingValues(
                                            horizontal = MaterialTheme.spacing.Large,
                                            vertical = 0.dp
                                        ),
                                        text = { Text(text = option.displayName.asString()) },
                                        onClick = {
                                            onUIEvent(CreateHomeworkUIEvent.OnDeadlineReminderChanged(option))
                                            showDropdown = false
                                        }
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = MaterialTheme.spacing.Large,
                                        vertical = 0.dp
                                    ),
                                    text = { Text(text = stringResource(R.string.custom_triple_dot)) },
                                    onClick = {
                                        onUIEvent(CreateHomeworkUIEvent.OnCustomDeadlineReminderButtonClicked)
                                        showDropdown = false
                                    }
                                )
                            }
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
                            onUIEvent(CreateHomeworkUIEvent.OnNotesChanged(it))
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

@Preview(showBackground = false)
@Composable
private fun CreateHomeworkScreenPreview() {
    HabitTheme {
        CreateHomeworkScreen(
            modifier = Modifier.fillMaxSize(),
            state = CreateHomeworkScreenUIState(
                isEditMode = true,
            ),
            onUIEvent = {}
        )
    }
}